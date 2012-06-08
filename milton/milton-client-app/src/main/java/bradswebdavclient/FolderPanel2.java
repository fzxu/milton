/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

/*
 * FolderPanel.java
 *
 * Created on February 13, 2008, 5:41 PM
 */
package bradswebdavclient;

import com.ettrema.httpclient.Folder;
import com.ettrema.httpclient.Resource;
import java.awt.Component;
import java.io.IOException;
import javax.swing.AbstractListModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import net.iharder.dnd.FileDrop;

/**
 *
 * @author  brad
 */
public class FolderPanel2 extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;
    FolderModel model;

    /** Creates new form FolderPanel */
    public FolderPanel2( final Folder folder ) {
        initComponents();
        model = new FolderModel( folder );
        table.setCellRenderer( new IconRenderer() );
        table.setLayoutOrientation( JList.VERTICAL_WRAP );

        new FileDrop( this, new FileDrop.Listener() {

            public void filesDropped( java.io.File[] files ) {
                for( java.io.File f : files ) {
                    System.out.println( f.getName() );
                    try {
                        folder.upload( f );
                    } catch( Exception ex ) {
                        throw new RuntimeException( ex );
                    }
                }
            }   // end filesDropped
        } ); // end FileDrop.Listener
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    jScrollPane1 = new javax.swing.JScrollPane();
    table = new javax.swing.JList();

    setName("Form"); // NOI18N
    setLayout(new java.awt.GridBagLayout());

    jScrollPane1.setName("jScrollPane1"); // NOI18N

    table.setModel(new javax.swing.AbstractListModel() {
      String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
      public int getSize() { return strings.length; }
      public Object getElementAt(int i) { return strings[i]; }
    });
    table.setMaximumSize(new java.awt.Dimension(6000, 8000));
    table.setName("table"); // NOI18N
    table.setVisibleRowCount(100);
    jScrollPane1.setViewportView(table);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    add(jScrollPane1, gridBagConstraints);
  }// </editor-fold>//GEN-END:initComponents
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JList table;
  // End of variables declaration//GEN-END:variables

    class FolderModel extends AbstractListModel {
        private static final long serialVersionUID = 1L;

        final Folder folder;

        public FolderModel( Folder folder ) {
            this.folder = folder;
        }

        Resource getResource( int row ) {
            try {
                return folder.children().get( row );
            } catch( Exception ex ) {
                throw new RuntimeException( ex );
            }
        }

        public int getSize() {
            try {
                return folder.children().size();
            } catch( Exception ex ) {
                throw new RuntimeException( ex );
            }
        }

        public Object getElementAt( int index ) {
            return getResource( index );
        }
    }

    class IconRenderer extends JLabel implements ListCellRenderer {
        private static final long serialVersionUID = 1L;

        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
            Resource r = (Resource) value;
            String iconName;
            if( r instanceof Folder ) {
                iconName = FolderNode.ICON_FOLDER;
            } else {
                iconName = "/s_file.png";
            }
            Icon icon = MyCellRenderer.getIcon( iconName );
            setIcon( icon );
            setText( r.name );
            return this;
        }
    }
}
