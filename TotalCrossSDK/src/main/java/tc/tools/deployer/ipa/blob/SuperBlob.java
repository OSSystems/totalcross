/*********************************************************************************
 *  TotalCross Software Development Kit                                          *
 *  Copyright (C) 2012 SuperWaba Ltda.                                      *
 *  Copyright (C) 2012-2020 TotalCross Global Mobile Platform Ltda.              *
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

package tc.tools.deployer.ipa.blob;

import java.io.IOException;
import java.util.Vector;

import tc.tools.deployer.ipa.ElephantMemoryReader;
import tc.tools.deployer.ipa.ElephantMemoryWriter;

public class SuperBlob extends BlobCore {
  Vector<BlobIndex> index = new Vector<BlobIndex>();

  protected SuperBlob(long magic) {
    super(magic);
  }

  public void add(BlobIndex blobIndex) {
    index.addElement(blobIndex);
  }

  @Override
  protected void writeToStream(ElephantMemoryWriter writer) throws IOException {
    int count = index.size();
    writer.writeUnsignedInt(count);

    long idxPos = writer.pos;
    writer.pos += count * 8L;
    for (int i = 0; i < count; i++) {
      BlobIndex item = (BlobIndex) index.elementAt(i);
      item.offset = writer.pos - offset;
      writer.memorize();
      writer.moveTo(idxPos);
      writer.writeUnsignedInt(item.blobType);
      writer.writeUnsignedInt(item.offset);
      idxPos = writer.pos;
      writer.moveBack();
      BlobHandler.writeBlob(item.blob, writer);
    }
  }

  @Override
  protected void readFromStream(ElephantMemoryReader reader)
      throws IOException, InstantiationException, IllegalAccessException {
    long count = reader.readUnsignedInt();
    for (long i = 0; i < count; i++) {
      long blobType = reader.readUnsignedInt();
      long blobOffset = reader.readUnsignedInt();

      reader.memorize();
      reader.moveTo(offset + blobOffset);
      BlobCore blob = BlobHandler.readBlob(reader);
      reader.moveBack();

      add(new BlobIndex(blobType, blob));
    }
  }
}
