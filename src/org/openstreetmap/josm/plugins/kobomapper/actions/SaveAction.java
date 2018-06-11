package org.openstreetmap.josm.plugins.kobomapper.actions;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.mapmode.MapMode;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.tools.GBC;
import org.openstreetmap.josm.tools.ImageProvider;
import org.openstreetmap.josm.tools.Logging;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static org.openstreetmap.josm.tools.I18n.tr;

public class SaveAction extends MapMode {

    private JRadioButton plusOne = new JRadioButton("+1", false);
    private JRadioButton plusTwo = new JRadioButton("+2", true); // enable this by default
    private JRadioButton minusOne = new JRadioButton("-1", false);
    private JRadioButton minusTwo = new JRadioButton("-2", false);
    final JCheckBox tagPolygon = new JCheckBox(tr("on polygon"));

    JDialog dialog;
    JButton clearButton;
    final JTextField inputNumber = new JTextField();
    final JTextField inputStreet = new JTextField();
    JLabel link = new JLabel();


    public SaveAction() {
        super(tr("Save"), "buildings",
                tr("Save Cahanges"),
                // CHECKSTYLE.OFF: LineLength
                null,
                // CHECKSTYLE.ON: LineLength
                getCursor());


    }


    private static Cursor getCursor() {
        try {
            return ImageProvider.getCursor("crosshair", null);
        } catch (RuntimeException e) {
            Logging.warn(e);
        }
        return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
    }

    @Override public void enterMode() {
        super.enterMode();
        if (dialog == null) {
            createDialog();
        }
        dialog.setVisible(true);
        MainApplication.getMap().mapView.addMouseListener(this);
    }

    @Override public void exitMode() {
        if (MainApplication.getMap().mapView != null) {
            super.exitMode();
            MainApplication.getMap().mapView.removeMouseListener(this);
        }
        // kill the window completely to fix an issue on some linux distro and full screen mode.
        if (dialog != null) {
            dialog.dispose();
            dialog = null;
        }
    }


    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1)
            return;
        updateKeyModifiers(e);
        MapView mv = MainApplication.getMap().mapView;

    }

    private void createDialog() {
        ImageIcon iconLink = ImageProvider.get(null, "Mf_relation");
        link.setIcon(iconLink);
        link.setEnabled(false);
        JPanel p = new JPanel(new GridBagLayout());
        JLabel number = new JLabel(tr("Next no"));
        JLabel street = new JLabel(tr("Street"));
        p.add(number, GBC.std().insets(0, 0, 0, 0));
        p.add(inputNumber, GBC.eol().fill(GBC.HORIZONTAL).insets(5, 5, 0, 5));
        p.add(street, GBC.std().insets(0, 0, 0, 0));
        JPanel p2 = new JPanel(new GridBagLayout());
        inputStreet.setEditable(false);
        p2.add(inputStreet, GBC.std().fill(GBC.HORIZONTAL).insets(5, 0, 0, 0));
        p2.add(link, GBC.eol().insets(10, 0, 0, 0));
        p.add(p2, GBC.eol().fill(GBC.HORIZONTAL));
        clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputNumber.setText("");
                inputStreet.setText("");

            }
        });
        ButtonGroup bgIncremental = new ButtonGroup();
        bgIncremental.add(plusOne);
        bgIncremental.add(plusTwo);
        bgIncremental.add(minusOne);
        bgIncremental.add(minusTwo);
        p.add(minusOne, GBC.std().insets(10, 0, 10, 0));
        p.add(plusOne, GBC.std().insets(0, 0, 10, 0));


        final Object[] options = {};
        final JOptionPane pane = new JOptionPane(p,
                JOptionPane.PLAIN_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION,
                null, options, null);
        dialog = pane.createDialog(Main.parent, tr("Enter addresses"));
        dialog.setModal(false);
        dialog.setAlwaysOnTop(true);
        dialog.addComponentListener(new ComponentAdapter() {
            protected void rememberGeometry() {
                Main.pref.put("cadastrewms.addr.bounds", dialog.getX()+","+dialog.getY()+","+dialog.getWidth()+","+dialog.getHeight());
            }

            @Override public void componentMoved(ComponentEvent e) {
                rememberGeometry();
            }

            @Override public void componentResized(ComponentEvent e) {
                rememberGeometry();
            }
        });
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg) {
                MainApplication.getMap().selectMapMode((MapMode) MainApplication.getMap().getDefaultButtonAction());
            }
        });
        String bounds = Main.pref.get("cadastrewms.addr.bounds", null);
        if (bounds != null) {
            String[] b = bounds.split(",");
            dialog.setBounds(new Rectangle(
                    Integer.parseInt(b[0]), Integer.parseInt(b[1]), Integer.parseInt(b[2]), Integer.parseInt(b[3])));
        }
    }

}
