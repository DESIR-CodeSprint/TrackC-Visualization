# Track C: Visualization of processed data with added dimensions for journals, topics, or reference dependency graphs

One of the major substantial outcomes of the previous DESIR Code Sprint Track-C was the novel generic concept of time dependent graphs of relations and its visual presentation. Examples of such graphs may be co-authorship and citation graphs, genealogy trees, or characters interaction graphs. 
From the visual perspective both the structure and time characteristics of such graphs play a significant analytical role. Our web-based tool developed throughout DESIR project now holds a functionality of visualizing bibliographical datasets (e.g imported via BibSonomy API or loaded from a file), on top of the generic data model. 


## Track goal
Within this Code Sprint we will focus on the extension of our tool both towards new data formats and use cases, as well as new visual forms. The participants will have the opportunity to work on the mapping of different data to the generic model of our graphs and/or on the translation of data formats to intermediate RDF description (subject-predicate-object). Bring-Your-Own-Data model is encouraged. New visual forms will cover the modification of web application user interface to include additional visualizations of metadata or aggregated information. 

## Agenda
1) General introduction to visualization
2) Graphs and graph visualization
3) Temporal graphs concept
4) Example: temporal graph visualization in DESIR web app
5) Example: temporal graph visualization in VisNow 
6) Code sprint projects overview
7) Tech-talk: intermediate data model
8) Tech-talk: RDF 
9) Tech-talk: backend-fronted data communication


## Track projects choice
1) Create your own test data directly in data model (hardcoded)
2) Create your own data source (API or file) and map it to data model
3) Translate your own data source (API or file) to RDF format
4) Advance RDF parsing to understand new commands
5) Create data model export to RDF
6) Work on automated translation of data model to DataObject (*)
7) Enhance BibSonomy query

For new data sources consider one of our three usage scenarios:
1) Bibliographical data
2) Genealogy
3) Character interactions (play/movie,etc.) - you can use the data source provided below

## Prerequisites 
The substantial and technical skills prerequisites depend on the choice of the project and cover one of the following topics:
- Java programming
- Java web programming frameworks (e.g. Spring/SpringBoot)
- General web frontend programming (e.g Angular, JavaScript)

## Technical preparation
To run all steps of our track and work with codes the following components are required:
- 64-bit Windows, Mac OS or Linux operating system. 
- Java JDK 8 64-bit - we suggest downloading from Oracle website [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html). 
- NetBeans IDE v8.2 for Java EE - download [here](https://netbeans.org/downloads/). Note that all provided projects are in NetBeans project format. If you wish to use another Java IDE you need to convert the projects by yourself. 
- [BibSonomy](https://www.bibsonomy.org) account and API key will be required for API querying.
- Project sources and presentations from this Git repository. You can pull it directly from NetBeans through Team->Git->Clone and providing URL: https://github.com/DARIAH-ERIC/DESIR-CodeSprint-TrackC-Visualization
- You need your GitHub user to commit to repository. Please use your own branch for your work. We suggest branch named "feature/<your_user>".

## Links
- [Data model description + RDF examples]()
- [BibSonomy REST API](https://bitbucket.org/bibsonomy/bibsonomy/wiki/documentation/api/REST%20API)
- [Apache Jena](http://jena.apache.org/documentation/io/index.html)
- [Apache Jena RDF output](https://jena.apache.org/documentation/io/rdf-output.html)
- [Track C Googledocs]()
- [Track C Results presentation]()

##Additional links
- [VisNow project](https://gitlab.com/ICM-VisLab/VisNow)
- [JLargeArrays project](https://gitlab.com/ICM-VisLab/JLargeArrays)
- [JLargeArrays API javadoc](http://javadoc.io/doc/pl.edu.icm/JLargeArrays/1.6)
- [JSciC project](https://gitlab.com/ICM-VisLab/JSciC)
- [JSciC API javadoc](http://javadoc.io/doc/pl.edu.icm/JSciC/1.0)
- [VisNow page](https://visnow.icm.edu.pl/)
- [D3.js library for 2D web visualization](https://d3js.org)
- [Vega and Vega-lite for 2D web visualization](https://vega.github.io)
- [Comac Navigator](http://devel.comac.ceon.pl/comac-navigator-latest/?graph=6b4f66cc) as example of simple 2D graph layout
- [Gephi - 3D graph visualization tool for desktop](https://gephi.org)
- [Gephi video](https://player.vimeo.com/video/9726202)
- [Walrus - 3D graph visualization tool for desktop](https://www.caida.org/tools/visualization/walrus/)
- [WebGL](https://www.khronos.org/webgl/)
- [three.js](https://threejs.org)


## Contact
- Bartosz Borucki (<b.borucki@icm.edu.pl>)
- Tomasz Blazejczyk (<t.blazejczyk@icm.edu.pl>)
