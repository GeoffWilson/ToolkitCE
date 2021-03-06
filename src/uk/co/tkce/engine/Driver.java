package uk.co.tkce.engine;

import uk.co.tkce.toolkit.types.Project;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 * Creates an Engine instance and runs the selected game
 *
 * @author geoff wilson (contact at gawilson.net)
 * @version svn
 */
public class Driver
{
    public static void main(String[] args)
    {
       // new GameControl(new Project(new File("D:/Programming/toolkit_ce/game/sf2/sf2.gam")));

        JFileChooser jfc = new JFileChooser();
        jfc.setFileFilter(new FileNameExtensionFilter("Main File", "gam"));

        jfc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
        {
            new GameControl(new Project(jfc.getSelectedFile()));
        }
    }
}
