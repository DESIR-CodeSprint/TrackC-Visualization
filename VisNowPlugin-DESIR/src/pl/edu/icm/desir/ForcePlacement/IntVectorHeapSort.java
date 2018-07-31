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

package pl.edu.icm.desir.ForcePlacement;

/**
 * @author Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class IntVectorHeapSort
{

    private final int[] sortedItems;
    private final int[] indices;
    private final int nSortedItems;
    private final int veclen;
    private int n;
    private int left;
    private int right;
    private int largest;

    private IntVectorHeapSort(int[] sortedItems, int[] indices, int veclen)
    {
        this.sortedItems = sortedItems;
        this.veclen = veclen;
        this.indices = indices;
        n = nSortedItems = indices.length;
    }

    private void buildHeap(int[] a)
    {
        n = nSortedItems - 1;
        for (int i = n / 2; i >= 0; i--)
            maxHeap(a, i);
    }

    private boolean gt(int i, int j)
    {
        for (int k = 0, k0 = veclen * i, k1 = veclen * j; k < veclen; k++, k0++, k1++) {
            if (sortedItems[k0] < sortedItems[k1])
                return false;
            else if (sortedItems[k0] > sortedItems[k1])
                return true;
        }
        return false;
    }

    private void maxHeap(int[] a, int i)
    {
        left = 2 * i;
        right = 2 * i + 1;
        //      if (left <= n && sortedItems[left] > sortedItems[i])
        if (left <= n && gt(left, i))
            largest = left;
        else
            largest = i;

        //      if (right <= n && sortedItems[right] > sortedItems[largest])
        if (right <= n && gt(right, largest))
            largest = right;
        if (largest != i) {
            exchange(i, largest);
            maxHeap(a, largest);
        }
    }

    private void exchange(int i, int j)
    {
        int t;
        for (int k = 0, k0 = veclen * i, k1 = veclen * j; k < veclen; k++, k0++, k1++) {
            t = sortedItems[k0];
            sortedItems[k0] = sortedItems[k1];
            sortedItems[k1] = t;
        }
        t = indices[i];
        indices[i] = indices[j];
        indices[j] = t;
    }
    
    private void sort()
    {
        buildHeap(sortedItems);
        for (int i = n; i > 0; i--) {
            exchange(0, i);
            n -= 1;
            maxHeap(sortedItems, 0);
        }
    }
    
    public static void sort(int[] sortedItems, int[] indices, int veclen)
    {
        if (indices.length * veclen != sortedItems.length) {
            System.out.println("bad table lengths");
            return;
        }
        new IntVectorHeapSort(sortedItems, indices, veclen).sort();
    }
}
