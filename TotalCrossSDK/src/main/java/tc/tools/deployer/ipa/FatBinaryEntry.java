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

package tc.tools.deployer.ipa;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.x509.X509Store;

public class FatBinaryEntry {
  protected long cputype;
  protected long cpusubtype;
  protected long offset;
  protected long size;
  protected long alignment;
  protected AppleBinary file;

  public FatBinaryEntry(ElephantMemoryReader reader)
      throws IOException, InstantiationException, IllegalAccessException {
    cputype = reader.readUnsignedInt();
    cpusubtype = reader.readUnsignedInt();
    offset = reader.readUnsignedInt();
    size = reader.readUnsignedInt();
    alignment = reader.readUnsignedInt();

    file = AppleBinary.create(reader.copy((int) offset, (int) (offset + size)));
  }

  public void resign(ElephantMemoryWriter writer, KeyStore ks, X509Store certStore, String bundleIdentifier,
      byte[] entitlementsBytes, byte[] info, byte[] sourceData)
      throws UnrecoverableKeyException, CertificateEncodingException, KeyStoreException, NoSuchAlgorithmException,
      OperatorCreationException, IOException, CMSException {
    writer.align((int) offset);
    byte[] resignedFile = file.resign(ks, certStore, bundleIdentifier, entitlementsBytes, info, sourceData);
    size = resignedFile.length;
    writer.write(resignedFile);
  }

  public void writeHeader(ElephantMemoryWriter writer) throws IOException {
    writer.writeUnsignedInt(cputype);
    writer.writeUnsignedInt(cpusubtype);
    writer.writeUnsignedInt(offset);
    writer.writeUnsignedInt(size);
    writer.writeUnsignedInt(alignment);
  }
}
