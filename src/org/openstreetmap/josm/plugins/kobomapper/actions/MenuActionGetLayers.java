// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.kobomapper.actions;

import static org.openstreetmap.josm.tools.I18n.marktr;
import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;

import org.openstreetmap.josm.actions.JosmAction;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.openstreetmap.josm.tools.GBC;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.plugins.kobomapper.utils.GeoserverApi;

import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.MainApplication;


@SuppressWarnings("serial")
public class MenuActionGetLayers extends JosmAction {

    public static final String NAME = marktr("Get Plot Boundaries");
    //MapView mv = MainApplication.getMap().mapView;

        private static final String[] groups = {
        "", tr("All Groups"),
        "001", "Group 1",  "002", "Group 2","003", "Group 3","004", "Group 4","005", "Group 5"};

        private static final String[] filters = {
        "", tr("All Time"),
        "001", "Today",  "002", "Yesterday","003", "This Week","004", "Last 2 weeks"};


    public MenuActionGetLayers() {
        super(tr(NAME), null, tr("Get Plot boundaries"), null, false);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        addNewLayer();
        GeoserverApi client = new GeoserverApi();
        client.getJson();

        
           
    }


    public static void addNewLayer() {

        String filter;
        String group;

        JLabel labelSectionNewLayer = new JLabel(tr("Add new mapped boundaries layer"));
        JPanel p = new JPanel(new GridBagLayout());
        JLabel labelGroup = new JLabel(tr("Group"));
        final JComboBox<String> inputGroup = new JComboBox<>();
        final JComboBox<String> inputFilter = new JComboBox<>();
        for (int i = 1; i < groups.length; i += 2) {
            inputGroup.addItem(groups[i]);
        }
        inputGroup.setToolTipText(tr("<html>Group</html>"));
        if (!Main.pref.get("kobo.codegroup").equals("")) {
            for (int i = 0; i < groups.length; i += 2) {
                if (groups[i].equals(Main.pref.get("kobo.codegroup")))
                    inputGroup.setSelectedIndex(i/2);
        
            }
        }

        JLabel labelFilter = new JLabel(tr("Filter"));
        for (int i = 1; i < filters.length; i += 2) {
            inputFilter.addItem(filters[i]);
        }
        inputFilter.setToolTipText(tr("<html>Time Period</html>"));
        if (!Main.pref.get("kobo.filter").equals("")) {
            for (int i = 0; i < filters.length; i += 2) {
                if (filters[i].equals(Main.pref.get("kobo.cfilter")))
                    inputFilter.setSelectedIndex(i/2);
        
            }
        }

        p.add(labelSectionNewLayer, GBC.eol());
        p.add(labelGroup, GBC.std().insets(10, 0, 0, 0));
        p.add(inputGroup, GBC.eol().fill(GBC.HORIZONTAL).insets(5, 0, 0, 5));
        p.add(labelFilter, GBC.std().insets(10, 0, 0, 0));
        p.add(inputFilter, GBC.eol().fill(GBC.HORIZONTAL).insets(5, 0, 0, 5));
        JOptionPane pane = new JOptionPane(p, JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null) {
            private static final long serialVersionUID = 1L;

            @Override
            public void selectInitialValue() {
                //inputTown.requestFocusInWindow();
                //inputTown.selectAll();
            }
        };

        pane.createDialog(Main.parent, tr("Download Plot Boundaries")).setVisible(true);


        group = groups[inputGroup.getSelectedIndex()*2];
        Main.pref.put("kobo.group", group);

        filter = groups[inputFilter.getSelectedIndex()*2];
        Main.pref.put("kobo.filter", filter);

        //GeoserverApi client = new GeoserverApi();





    }
}
