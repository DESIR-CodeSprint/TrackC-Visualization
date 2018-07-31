/* ***** BEGIN LICENSE BLOCK *****
 *  
 * VisNowPlugin-DESIR
 * Copyright (C) 2018 onward University of Warsaw, ICM
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * ***** END LICENSE BLOCK ***** */
package pl.edu.icm.desir.ReadBibSonomy;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bibsonomy.common.enums.Filter;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.data.NoDataAccessor;
import org.bibsonomy.rest.client.RestLogicFactory;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RendererFactory;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.rest.renderer.UrlRenderer;
import pl.edu.icm.jlargearrays.FloatLargeArray;
import pl.edu.icm.jlargearrays.IntLargeArray;
import pl.edu.icm.jlargearrays.LargeArrayType;
import pl.edu.icm.jlargearrays.LargeArrayUtils;
import pl.edu.icm.jlargearrays.ObjectLargeArray;
import pl.edu.icm.jlargearrays.StringLargeArray;
import pl.edu.icm.jscic.CellArray;
import pl.edu.icm.jscic.CellSet;
import pl.edu.icm.jscic.IrregularField;
import pl.edu.icm.jscic.cells.CellType;
import pl.edu.icm.jscic.dataarrays.DataArray;
import pl.edu.icm.jscic.dataarrays.DataArraySchema;
import pl.edu.icm.jscic.dataarrays.DataArrayType;
import pl.edu.icm.jscic.dataarrays.IntDataArray;
import pl.edu.icm.jscic.dataarrays.ObjectDataArray;
import pl.edu.icm.jscic.dataarrays.StringDataArray;
import pl.edu.icm.desir.ForcePlacement.IntVectorHeapSort;

/**
 * Set of static methods for generating VisNow field from BibSonomy posts.
 *
 * @author Piotr Wendykier (piotrw@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class ReadBibSonomyCore
{

    /**
     * Generates a field that represents a coauthorship graph from BibSonomy posts using REST API.
     *
     * @param login    user login
     * @param apikey   API key corresponding to the user login
     * @param userName user name for the GroupingEntity.USER
     * @param tags     list of tags by which to retrieve posts
     *
     * @return field that represents a coauthorship
     */
    public static IrregularField generateCoauthorshipUsingREST(String login, String apikey, String userName, List<String> tags)
    {
        return generateCoauthorshipUsingREST(login, apikey, GroupingEntity.USER, userName, tags, "", null, null, null, Order.ADDED, null, null, 0, 1000);
    }

    /**
     * Generates a field that represents a coauthorship graph from BibSonomy posts using REST API.
     *
     * @param login        user login
     * @param apikey       API key corresponding to the user login
     * @param grouping     grouping tells whom posts are to be shown: the posts of a
     *                     user, of a group or of the viewables.
     * @param groupingName name of the grouping. if grouping is user, then its the
     *                     username. if grouping is set to {@link GroupingEntity#ALL},
     *                     then its an empty string!
     * @param tags         list of tags by which to retrieve posts
     * @param hash         hash value of a resource, if one would like to get a list of
     *                     all posts belonging to a given resource. if unused, its empty
     *                     but not null.
     * @param search       free text search
     * @param searchType   whether to search locally or using an index shared by several systems
     * @param filters      filter for the retrieved posts
     * @param order        a flag indicating the way of sorting
     * @param startDate    if given, only posts that have been created after (inclusive) startDate are returned
     * @param endDate      if given, only posts that have been created before (inclusive) endDate are returned
     * @param start        inclusive start index of the view window
     * @param end          exclusive end index of the view window
     *
     * @return field that represents a coauthorship
     */
    public static IrregularField generateCoauthorshipUsingREST(String login, String apikey, GroupingEntity grouping, String groupingName, List<String> tags, String hash, String search, SearchType searchType, Set<Filter> filters, Order order, Date startDate, Date endDate, int start, int end)
    {
        final RestLogicFactory rlf = new RestLogicFactory();
        final LogicInterface logic = rlf.getLogicAccess(login, apikey);
        final List<Post<BibTex>> posts = logic.getPosts(BibTex.class, grouping, groupingName, tags, hash, search, searchType, filters, order, startDate, endDate, start, end);
        return generateFieldFromPosts(posts);
    }

    /**
     * Generates a field that represents a coauthorship graph from BibSonomy posts stored in a file.
     *
     * @param filePath file path to JSON or XML file containing posts exported from BibSonomy
     *
     * @return field that represents a coauthorship
     *
     * @throws IOException
     */
    public static IrregularField generateCoauthorshipFromFile(String filePath) throws IOException
    {
        final List<Post<? extends Resource>> posts = parseDocument(filePath);
        final List<Post<BibTex>> bposts = new ArrayList<>(posts.size());
        for (final Post<? extends Resource> post : posts) {
            bposts.add((Post<BibTex>) post);
        }
        return generateFieldFromPosts(bposts);
    }

    private static List<Post<? extends Resource>> parseDocument(final String fileName) throws IOException
    {
        final RenderingFormat renderingFormat;
        final UrlRenderer urlRenderer = new UrlRenderer("");
        final RendererFactory rendererFactory = new RendererFactory(urlRenderer);
        if (fileName.toLowerCase().endsWith(".json")) {
            renderingFormat = RenderingFormat.JSON;
        } else {
            renderingFormat = RenderingFormat.XML;
        }

        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "utf-8"));

        try {
            return rendererFactory.getRenderer(renderingFormat).parsePostList(reader, NoDataAccessor.getInstance());
        } catch (final InternServerException ex) {
            reader.close();
            throw new BadRequestOrResponseException(ex);
        }
    }

    private static IrregularField generateFieldFromPosts(List<Post<BibTex>> posts)
    {
        Set<PersonName> s_authors = new HashSet<>();
        List<Coauthorship> edges = new ArrayList<>();
        int idx;
        for (final Post<BibTex> post : posts) {
            List<PersonName> authors = post.getResource().getAuthor();
            s_authors.addAll(authors);
            for (PersonName author1 : authors) {
                for (PersonName author2 : authors) {
                    if (!author1.equals(author2)) {
                        if ((idx = edges.indexOf(new Coauthorship(author1, author2))) > -1) {
                            edges.get(idx).addTitle(post.getResource().getTitle());
                        } else if ((idx = edges.indexOf(new Coauthorship(author2, author1))) > -1) {
                            edges.get(idx).addTitle(post.getResource().getTitle());
                        } else {
                            Coauthorship edge = new Coauthorship(author1, author2);
                            edge.addTitle(post.getResource().getTitle());
                            edges.add(edge);
                        }
                    }
                }
            }
        }

        int nauthors = s_authors.size();
        ObjectLargeArray la_authors = new ObjectLargeArray(nauthors);
        StringLargeArray la_names = new StringLargeArray(nauthors);
        IntLargeArray la_ids = new IntLargeArray(nauthors);
        int i = 0;
        for (PersonName next : s_authors) {
            la_authors.set(i, new Author(next));
            la_names.set(i, next.getFirstName() + " " + next.getLastName());
            la_ids.set(i, i);
            i++;
        }
        IrregularField field = new IrregularField(nauthors);
        field.setCurrentCoords((FloatLargeArray) LargeArrayUtils.generateRandom(LargeArrayType.FLOAT, 3 * nauthors));
        ObjectDataArray authors = new ObjectDataArray(la_authors, new DataArraySchema("authors", DataArrayType.FIELD_DATA_OBJECT, nauthors, 1));
        field.addComponent(authors);
        StringDataArray names = new StringDataArray(la_names, new DataArraySchema("names", DataArrayType.FIELD_DATA_STRING, nauthors, 1));
        field.addComponent(names);
        IntDataArray ids = new IntDataArray(la_ids, new DataArraySchema("ids", DataArrayType.FIELD_DATA_INT, nauthors, 1));
        field.addComponent(ids);

        CellSet cs_coauthorship = new CellSet("coauthorship");
        int[] segments = new int[edges.size() * 2];
        int[] indices = new int[edges.size()];
        for (int j = 0; j < indices.length; j++)
            indices[j] = j;
        int i1, i2, s = 0;
        ObjectLargeArray la_edges = new ObjectLargeArray(edges.size());
        for (Coauthorship edge : edges) {
            la_edges.set(s, edge);
            PersonName a1 = edge.author1;
            PersonName a2 = edge.author2;
            i1 = -1;
            i2 = -1;
            for (int j = 0; j < nauthors; j++) {
                PersonName a3 = ((Author) la_authors.get(j)).getAuthorName();
                if (i1 < 0 && a3.equals(a1)) {
                    i1 = j;
                }
                if (i2 < 0 && a3.equals(a2)) {
                    i2 = j;
                }
                if (i1 >= 0 && i2 >= 0) {
                    break;
                }
            }
            if (i1 < i2) {
                segments[2 * s] = i1;
                segments[2 * s + 1] = i2;
            }
            else {
                segments[2 * s] = i2;
                segments[2 * s + 1] = i1;
            }
            s++;
        }
        IntVectorHeapSort.sort(segments, indices, 2);
        CellArray ca_coauthorship = new CellArray(CellType.SEGMENT, segments, null, null);
        ca_coauthorship.setDataIndices(indices);
        cs_coauthorship.setCellArray(ca_coauthorship);
        cs_coauthorship.addComponent(DataArray.create(la_edges, 1, "edges"));
        float[] degrees = new float[edges.size()];
        for (int j = 0; j < degrees.length; j++) 
            degrees[j] = ((Coauthorship)la_edges.get(j)).toFloat();
        cs_coauthorship.addComponent(DataArray.create(degrees, 1, "edge_degree"));
        field.addCellSet(cs_coauthorship);

        return field;
    }

}
