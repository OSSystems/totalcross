/*********************************************************************************
 *  TotalCross Software Development Kit                                          *
 *  Copyright (C) 2017-2020 TotalCross Global Mobile Platform Ltda.              *
 *  All Rights Reserved                                                          *
 *                                                                               *
 *  This library and virtual machine is distributed in the hope that it will     *
 *  be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.                         *
 *                                                                               *
 *  This file is covered by the GNU LESSER GENERAL PUBLIC LICENSE VERSION 2.1    *
 *  A copy of this license is located in file license.txt at the root of this    *
 *  SDK or can be downloaded here:                                               *
 *  http://www.gnu.org/licenses/lgpl-2.1.txt                                     *
 *                                                                               *
 *********************************************************************************/

package jdkcompat.util.concurrent;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class ConcurrentLinkedQueue4D<E> extends AbstractQueue<E> implements Queue<E>, Serializable {

  private static final long serialVersionUID = 1365616274026915983L;
  /*
   * TODO use a real concurrent stuff, not just this placeholder here
   */
  private LinkedList<E> list;

  public ConcurrentLinkedQueue4D() {
    this(new LinkedList<E>());
  }

  public ConcurrentLinkedQueue4D(Collection<? extends E> c) {
    list = new LinkedList<>(c);
  }

  @Override
  public boolean offer(E e) {
    list.add(e);
    return true;
  }

  @Override
  public E poll() {
    return list.poll();
  }

  @Override
  public E peek() {
    return list.peek();
  }

  @Override
  public Iterator<E> iterator() {
    return list.iterator();
  }

  @Override
  public int size() {
    return list.size();
  }

}
