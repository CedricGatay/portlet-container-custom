/*
 * CDDL HEADER START
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.sun.com/cddl/cddl.html and legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 * CDDL HEADER END
 */

package com.sun.portal.portletcontainer.installer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

public class GUIInstaller  extends JPanel {
    private static Logger logger = Logger.getLogger(GUIInstaller.class.getPackage().getName(),
            "PCSDKLogMessages");    
    final JLabel lblContainer = new JLabel();

    private JComboBox containerType;

    final JLabel lblInstallDirectory = new JLabel();

    private JTextField baseDirectory;

    private JLabel lblDomainDirectory = new JLabel();

    private JTextField domainDirectory;
    
    private JFileChooser fc ;
    
    private JButton btnAntHome;
    
    private JTextField antHome;
    
    private JButton okButton ;
    
    private PortletContainerConfigurator configurator;
    
    public GUIInstaller(PortletContainerConfigurator configurator) {
        this.configurator = configurator;
    }

    public void init(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        initializeUI();            
        JFrame f = new JFrame(InstallerMessages.getString("PortletContainerConfigurator.PanelTitle"));
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setContentPane(this);
        f.setResizable(true);
        f.pack();
        f.setVisible(true);                   
    }
    
    private void changeButtonToQuitButton(){
	okButton.setText(InstallerMessages.getString("PortletContainerConfigurator.Quit"));
	okButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent event) {
		 System.exit(0);
	    }
	});
    }

    private void initializeUI() {
	String systemLafClass = UIManager.getSystemLookAndFeelClassName();
	try {
	    UIManager.setLookAndFeel(systemLafClass);
	    SwingUtilities.updateComponentTreeUI(this);
	} catch (Exception e) {
	    logger.log(Level.INFO, "set-landf-fail");
	}

	fc = new JFileChooser();

	setBorder(new LineBorder(Color.black, 1, false));
	setLayout(new GridBagLayout());

	setupContainerTypeUIFields();

	setupBaseDirUIFields();

	setpDomainDirUIFields();

	final JLabel lblStatus = new JLabel();
	lblStatus.setFont(new Font("", Font.BOLD, 12));
	lblStatus.setHorizontalAlignment(SwingConstants.LEFT);
	final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
	gridBagConstraints_1.anchor = GridBagConstraints.WEST;
	gridBagConstraints_1.ipadx = 40;
	gridBagConstraints_1.gridy = 5;
	gridBagConstraints_1.gridx = 0;
	add(lblStatus, gridBagConstraints_1);
	lblStatus.setText("Status:");

	final JLabel statusTextLabel = new JLabel();
	statusTextLabel.setFont(new Font("", Font.PLAIN, 12));
	statusTextLabel.setHorizontalTextPosition(SwingConstants.LEFT);
	final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
	gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
	gridBagConstraints_2.ipadx = 160;
	gridBagConstraints_2.anchor = GridBagConstraints.WEST;
	gridBagConstraints_2.gridy = 5;
	gridBagConstraints_2.gridx = 1;
	add(statusTextLabel, gridBagConstraints_2);

	okButton = new JButton();
	final GridBagConstraints gridBagConstraints = new GridBagConstraints();
	gridBagConstraints.gridy = 5;
	gridBagConstraints.gridx = 2;
	add(okButton, gridBagConstraints);
	okButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent event) {
		if (validateFields()) {
		    try {
			okButton.setEnabled(false);
			String ant = null;
			
			String selectedContainer = containerType.getSelectedItem().toString();
			if (selectedContainer.indexOf(Installer.TOMCAT) != -1 ) {
			    ant = antHome.getText();
			}

			File antDir = new File(baseDirectory.getText() + "/lib/ant");
			if(!antDir.exists()) {
				String antHomeEnvValue = System.getenv("ANT_HOME");
				if(antHomeEnvValue == null) {
					ant = antHome.getText();
				} else {
					ant = antHomeEnvValue;
				}
			}

			String domainRoot = domainDirectory.getText().trim();
			File domainRootDir = new File(domainRoot);
			if(!domainRootDir.isDirectory()) {
				JOptionPane.showMessageDialog(null, 
					InstallerMessages.getString("PortletContainerConfigurator.InvalidDomain", new String[] {domainRoot} ), "Information",
					JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			configurator.install(baseDirectory.getText().trim(), domainRoot, 
				containerType.getSelectedItem().toString(), ant);
			logger.info("configuration-successful");
			statusTextLabel.setText(InstallerMessages.getString("PortletContainerConfigurator.SucessMessage"));
			changeButtonToQuitButton();
		    } catch (Exception e) {
			logger.log(Level.SEVERE, "configuration-failed", e);
			statusTextLabel.setText(InstallerMessages.getString("PortletContainerConfigurator.FailMessage"));
		    } finally {
			okButton.setEnabled(true);
		    }
		} else {
		    JOptionPane.showMessageDialog(null, InstallerMessages.getString("PortletContainerConfigurator.ValidationFailMessage"), "Information",
			    JOptionPane.INFORMATION_MESSAGE);
		}
	    }

	    private boolean validateFields() {
		String BLANK = "";
		if (baseDirectory.getText().equals(BLANK) || domainDirectory.getText().equals(BLANK)) {
		    return false;
		}
		if (antHome.isEnabled() && antHome.getText().equals(BLANK)) {
		    return false;
		}

		return true;
	    }
	});
	okButton.setText(InstallerMessages.getString("PortletContainerConfigurator.Okay"));
    }

    private void setpDomainDirUIFields() {
	lblDomainDirectory.setText(PortletContainerConfigurator.DOMAIN_DIRECTORY);
	lblDomainDirectory.setFont(new Font("", Font.BOLD, 12));
	final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
	gridBagConstraints_4.anchor = GridBagConstraints.WEST;
	gridBagConstraints_4.gridy = 3;
	gridBagConstraints_4.gridx = 0;
	add(lblDomainDirectory, gridBagConstraints_4);

	domainDirectory = new JTextField();
	final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
	gridBagConstraints_5.fill = GridBagConstraints.HORIZONTAL;
	gridBagConstraints_5.anchor = GridBagConstraints.WEST;
	gridBagConstraints_5.gridy = 3;
	gridBagConstraints_5.gridx = 1;
	add(domainDirectory, gridBagConstraints_5);

	final JButton btnDomainDir = new JButton();
	btnDomainDir.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		if (!baseDirectory.getText().equals(""))
		    fc.setCurrentDirectory(new File(baseDirectory.getText()));

		int returnVal = fc.showOpenDialog(GUIInstaller.this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
		    domainDirectory.setText(fc.getSelectedFile().getAbsolutePath());
		}
	    }
	});
	btnDomainDir.setText("...");
	final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
	gridBagConstraints_8.gridy = 3;
	gridBagConstraints_8.gridx = 2;
	add(btnDomainDir, gridBagConstraints_8);
    }

    private void setupBaseDirUIFields() {
	final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
	gridBagConstraints_2.anchor = GridBagConstraints.WEST;
	gridBagConstraints_2.gridy = 2;
	gridBagConstraints_2.gridx = 0;

	final JLabel lblAntHome = new JLabel();
	lblAntHome.setFont(new Font("", Font.BOLD, 12));
	lblAntHome.setText("Ant Home");
	final GridBagConstraints gridBagConstraints = new GridBagConstraints();
	gridBagConstraints.anchor = GridBagConstraints.WEST;
	gridBagConstraints.gridy = 1;
	gridBagConstraints.gridx = 0;
	add(lblAntHome, gridBagConstraints);

	antHome = new JTextField();
	antHome.setText(System.getenv("ANT_HOME"));

	final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
	gridBagConstraints_1.fill = GridBagConstraints.BOTH;
	gridBagConstraints_1.ipadx = 15;
	gridBagConstraints_1.gridy = 1;
	gridBagConstraints_1.gridx = 1;
	add(antHome, gridBagConstraints_1);

	btnAntHome = new JButton();
	// By default Glassfish is the container in the combo box and hence
        // disable ant home.
	setAntHomeState(false);
	btnAntHome.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(GUIInstaller.this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
		    antHome.setText(fc.getSelectedFile().getAbsolutePath());
		}

	    }

	});
	btnAntHome.setText("...");
	final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
	gridBagConstraints_4.gridy = 1;
	gridBagConstraints_4.gridx = 2;
	add(btnAntHome, gridBagConstraints_4);
	add(lblInstallDirectory, gridBagConstraints_2);
	lblInstallDirectory.setFont(new Font("", Font.BOLD, 12));
	lblInstallDirectory.setText(InstallerMessages.getString("PortletContainerConfigurator.InstallBaseDir"));

	baseDirectory = new JTextField();
	baseDirectory.addFocusListener(new FocusAdapter() {
	    public void focusLost(FocusEvent arg0) {
			String selectedContainer = containerType.getSelectedItem().toString();
			if (selectedContainer.equals(Installer.GLASSFISH)) {
				domainDirectory.setText("" + baseDirectory.getText() + File.separator + "domains" + File.separator
					+ "domain1");
						// Check if the Glassfish has ANT, if not ask for it
						File antDir = new File(baseDirectory.getText() + "/lib/ant");
						if(!antDir.exists()) {
							String antHomeEnvValue = System.getenv("ANT_HOME");
							if(antHomeEnvValue == null) {
								setAntHomeState(true);
							}
						}
			} else {
				if (selectedContainer.equals(Installer.WEBLOGIC)) {
					domainDirectory.setText("" + baseDirectory.getText() + File.separator + "user_projects"
							+ File.separator + "domains" + File.separator + "domain1");
				} else {
					domainDirectory.setText("" + baseDirectory.getText() + File.separator + "webapps");
				}
			}
	    }
	});
	final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
	gridBagConstraints_3.insets = new Insets(0, 0, 0, 0);
	gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
	gridBagConstraints_3.ipadx = 15;
	gridBagConstraints_3.anchor = GridBagConstraints.WEST;
	gridBagConstraints_3.gridy = 2;
	gridBagConstraints_3.gridx = 1;
	add(baseDirectory, gridBagConstraints_3);

	final JButton btnBaseDir = new JButton();
	btnBaseDir.setText("...");
	btnBaseDir.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent event) {
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(GUIInstaller.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				baseDirectory.setText(fc.getSelectedFile().getAbsolutePath());
				String selectedContainer = containerType.getSelectedItem().toString();
				if (selectedContainer.equals(Installer.GLASSFISH)) {
					domainDirectory.setText("" + baseDirectory.getText() + File.separator + "domains"
						+ File.separator + "domain1");
					setAntHomeState(false);
					// Check if the Glassfish has ANT, if not ask for it
					File antDir = new File(baseDirectory.getText() + "/lib/ant");
					if(!antDir.exists()) {
						String antHomeEnvValue = System.getenv("ANT_HOME");
						if(antHomeEnvValue == null) {
							System.out.println("ANT_HOME not found");
							setAntHomeState(true);
						}
					}

				} else {
					if (selectedContainer.equals(Installer.WEBLOGIC)) {
						domainDirectory.setText("" + baseDirectory.getText() + File.separator + "user_projects"
								+ File.separator + "domains" + File.separator + "domain1");
					} else {
						domainDirectory.setText("" + baseDirectory.getText() + File.separator + "webapps");
					}
					antHome.setText(System.getenv("ANT_HOME"));
					setAntHomeState(true);
				}
			}
	    }
	});

	final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
	gridBagConstraints_7.gridy = 2;
	gridBagConstraints_7.gridx = 2;
	add(btnBaseDir, gridBagConstraints_7);
    }

    private void setAntHomeState(boolean state) {
		antHome.setEnabled(state);
		btnAntHome.setEnabled(state);
    }

    private void setupContainerTypeUIFields() {
	final GridBagConstraints gridBagConstraints = new GridBagConstraints();
	gridBagConstraints.anchor = GridBagConstraints.WEST;
	gridBagConstraints.gridy = 0;
	gridBagConstraints.gridx = 0;
	add(lblContainer, gridBagConstraints);
	lblContainer.setFont(new Font("", Font.BOLD, 12));
	lblContainer.setText(InstallerMessages.getString("PortletContainerConfigurator.ContainerType"));

	String[] dataContainerType = InstallerFactory.getSupportedContainers();

	containerType = new JComboBox(dataContainerType);
	containerType.setSelectedIndex(0);

	containerType.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent event) {
		JComboBox cb = (JComboBox) event.getSource();
		String selectedContainer = (String) cb.getSelectedItem();

			if (selectedContainer.equalsIgnoreCase(Installer.GLASSFISH)) {
				lblDomainDirectory.setText(PortletContainerConfigurator.DOMAIN_DIRECTORY);
				setAntHomeState(false);
			} else {
				if (selectedContainer.equalsIgnoreCase(Installer.WEBLOGIC)) {
					lblDomainDirectory.setText(PortletContainerConfigurator.DOMAIN_DIRECTORY);
				} else {
					lblDomainDirectory.setText(PortletContainerConfigurator.WEBAPPS_DIRECTORY);
				}
				setAntHomeState(true);
			}
	    }
	});
	final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
	gridBagConstraints_1.insets = new Insets(0, 0, 0, 120);
	gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
	gridBagConstraints_1.ipadx = 85;
	gridBagConstraints_1.anchor = GridBagConstraints.WEST;
	gridBagConstraints_1.gridx = 1;
	gridBagConstraints_1.gridy = 0;
	add(containerType, gridBagConstraints_1);
    }
    
}
