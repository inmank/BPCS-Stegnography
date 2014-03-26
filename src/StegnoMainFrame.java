import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.IndexColorModel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigInteger;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.filechooser.FileNameExtensionFilter;

import sun.awt.image.FileImageSource;


public class StegnoMainFrame extends JApplet implements ImageConsumer{

	private JButton 		stegAction;
	private JButton 		genKeys;
	private JButton 		inDataPathSelButton;
	private JButton 		inImagePathSelButton;
	private JButton 		outDataPathSelButton;
	private JButton 		keyFilePathSelButton;
	private JCheckBox 		compressCheckBox;
	private JCheckBox 		encryptCheckBox;
	private JLabel 			inDataLabel;
	private JLabel 			modLabel;
	private JLabel 			inImageLabel;
	private JLabel 			outDataLabel;
	private JLabel 			keyFileLabel;
	private JLabel 			priKeyLabel;
	private JLabel 			pubKeyLabel;
	private JPanel 			optionsPanel;
	private JPanel 			inputPanel;
	private JPanel 			ouputPanel;
	private JPanel 			fileSelectionPanel;
	private JPanel 			optionalParamPanel;
	private JPanel 			cryptParamPanel;
	private JRadioButton 	hideRadiButton;
	private JRadioButton 	rtrvRadioButton;
	private JTextField 		inDataPath;
	private JTextField 		modValue;
	private JTextField 		pubKeyValue;
	private JTextField 		inImagePath;
	private JTextField 		outDataPath;
	private JTextField 		statusBar;
	private JTextField 		keyFilePath;
	private JTextField 		priKayValue;
	private TextArea  		outTextArea;
	private JScrollPane 	outTextScrollPanel;
	private JLabel 			inputImage;
	private JScrollPane 	inputImageScrollPanel;
	private JLabel 		 	outputImage;

	private boolean isHide = true;
	private boolean isEncrypt = false;
	private boolean isCompress = false;

	int[] 			rgbPalette ;
	int 			numRGBvalues;
	ImageProducer   imagePro;

	public void init() {

		/*LookAndFeelInfo[] installedLAF = UIManager.getInstalledLookAndFeels();
		try {
			for( int i=0; i < installedLAF.length; i++)
			{
				if(installedLAF[i].toString().indexOf("Nimbus") != -1)
				{
					UIManager.setLookAndFeel(installedLAF[i].getClassName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		initComponents();
		setInitialValues(true);
	}

	private void initComponents() {

		optionsPanel 		= new JPanel();
		hideRadiButton 		= new JRadioButton();
		rtrvRadioButton 	= new JRadioButton();

		fileSelectionPanel 		= new JPanel();
		inDataLabel 			= new JLabel();
		inDataPath 				= new JTextField();
		inDataPathSelButton 	= new JButton();
		inImageLabel 			= new JLabel();
		inImagePath 			= new JTextField();
		inImagePathSelButton 	= new JButton();
		outDataLabel 			= new JLabel();
		outDataPath 			= new JTextField();
		outDataPathSelButton 	= new JButton();

		optionalParamPanel 	= new JPanel();
		compressCheckBox 	= new JCheckBox();
		encryptCheckBox 	= new JCheckBox();

		cryptParamPanel 		= new JPanel();
		keyFileLabel 			= new JLabel();
		keyFilePath 			= new JTextField();
		keyFilePathSelButton 	= new JButton();
		priKeyLabel 			= new JLabel();
		priKayValue 			= new JTextField();
		pubKeyLabel 			= new JLabel();
		pubKeyValue 			= new JTextField();
		modLabel 				= new JLabel();
		modValue 				= new JTextField();
		genKeys 				= new JButton();

		inputPanel 				= new JPanel();
		ouputPanel 				= new JPanel();
		outTextArea 			= new TextArea();
		outTextScrollPanel 		= new JScrollPane();
		inputImageScrollPanel 	= new JScrollPane();
		inputImage				= new JLabel();
		outputImage				= new JLabel();

		stegAction 		= new JButton();
		statusBar 		= new JTextField();

		optionsPanel.setBorder(BorderFactory.createTitledBorder("Input Options"));

		hideRadiButton.setText("Hide Action");
		hideRadiButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				hideRadiButton.setSelected(true);
				rtrvRadioButton.setSelected(false);
				setInitialValues(true);
				inDataPath.setEnabled(true);
				inDataPathSelButton.setEnabled(true);
				setStatusMessage("");
				outTextScrollPanel.setViewportView(outputImage);
			}
		});

		rtrvRadioButton.setText("Retrieve Action");
		rtrvRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				rtrvRadioButton.setSelected(true);
				hideRadiButton.setSelected(false);
				setInitialValues(false);
				inDataPath.setEnabled(false);
				inDataPathSelButton.setEnabled(false);
				setStatusMessage("");
				outTextScrollPanel.setViewportView(outTextArea);
			}
		});

		fileSelectionPanel.setBorder(BorderFactory.createTitledBorder("Select Files"));

		inDataLabel.setText("Select Data to hide:");

		inDataPathSelButton.setText("Browse");
		inDataPathSelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				selectFileAction("Select the Data File to hide", inDataPath, false, false);
			}
		});

		inImageLabel.setText("Select Input Image:");

		inImagePathSelButton.setText("Browse");
		inImagePathSelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				boolean isSelected = selectFileAction("Select a GIF Image File", inImagePath, true, false);
				if (isSelected){
					BufferedImage tempPicture = null;
					try {
						tempPicture = ImageIO.read(new File(inImagePath.getText()));
						ImageIcon tempIcon = new ImageIcon( tempPicture );
						inputImage.setIcon(tempIcon);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null,
								"Unable to Display Input Image.",
								"Information:",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});

		outDataPathSelButton.setText("Browse");
		outDataPathSelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				selectFileAction("Select the Output Image File", outDataPath, false, true);
			}
		});

		outDataLabel.setText("Select Output File:");

		GroupLayout jPanel4Layout = new GroupLayout(fileSelectionPanel);
		fileSelectionPanel.setLayout(jPanel4Layout);
		jPanel4Layout.setHorizontalGroup(
				jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(jPanel4Layout.createSequentialGroup()
						.addGap(6, 6, 6)
						.addComponent(outDataLabel)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(outDataPath, GroupLayout.PREFERRED_SIZE, 259, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(outDataPathSelButton, GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE))
						.addGroup(jPanel4Layout.createSequentialGroup()
								.addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
										.addGroup(GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
												.addComponent(inDataLabel)
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(inDataPath))
												.addGroup(GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
														.addComponent(inImageLabel)
														.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(inImagePath, GroupLayout.PREFERRED_SIZE, 259, GroupLayout.PREFERRED_SIZE)))
														.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
														.addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
																.addComponent(inImagePathSelButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																.addComponent(inDataPathSelButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
		);
		jPanel4Layout.setVerticalGroup(
				jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(jPanel4Layout.createSequentialGroup()
						.addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(inImageLabel)
								.addComponent(inImagePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(inImagePathSelButton))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(inDataLabel)
										.addComponent(inDataPath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(inDataPathSelButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(outDataLabel)
												.addComponent(outDataPath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(outDataPathSelButton)))
		);

		optionalParamPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder("Optional Parameters")));

		compressCheckBox.setText("Enable Compression\\Decompression");

		encryptCheckBox.setText("Enable Encryption\\Decryption");
		encryptCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if(encryptCheckBox.isSelected() == true){
					compressCheckBox.setSelected(true);
					compressCheckBox.setEnabled(false);
					DisableAllEncryptionParameters(false);
				} else {
					compressCheckBox.setSelected(false);
					compressCheckBox.setEnabled(true);
					DisableAllEncryptionParameters(true);
				}
			}
		});

		GroupLayout jPanel6Layout = new GroupLayout(optionalParamPanel);
		optionalParamPanel.setLayout(jPanel6Layout);
		jPanel6Layout.setHorizontalGroup(
				jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(jPanel6Layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(compressCheckBox)
								.addComponent(encryptCheckBox))
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		jPanel6Layout.setVerticalGroup(
				jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(jPanel6Layout.createSequentialGroup()
						.addComponent(compressCheckBox, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(encryptCheckBox)
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);

		cryptParamPanel.setBorder(BorderFactory.createTitledBorder("Encryption Attributes"));

		keyFileLabel.setText("Select Key File:");

		keyFilePathSelButton.setText("Browse");
		keyFilePathSelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				boolean isSelected = selectFileAction("Select a Valid Key File", keyFilePath, false, false);

				KeyValues keys = null;
				String keyFile = keyFilePath.getText();
				if (isSelected){
					try
					{ 
						InputStream fis=new FileInputStream(keyFile);
						ObjectInputStream ois =new ObjectInputStream(fis);
						keys=(KeyValues)ois.readObject();
						ois.close();
						fis.close();
					}catch(Exception rer){
						keyFilePath.setText("");					
						JOptionPane.showMessageDialog(null,
								"Please select a valid Key file",
								"Error:",
								JOptionPane.ERROR_MESSAGE);
					}

					if (keys instanceof KeyValues)
					{
						modValue.setText(keys.mod.toString());
						pubKeyValue.setText(keys.pubkey.toString());
						priKayValue.setText(keys.privkey.toString());
					}
				}
			}
		});

		priKeyLabel.setText("Private key:");
		pubKeyLabel.setText("Public Key:");
		modLabel.setText("Mod Value:");
		genKeys.setText("Generate Keys");

		genKeys.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				JTextField tempText = new JTextField();
				selectFileAction("Select location to store Key File", tempText, false, true);
				String outKeyFile = tempText.getText();
				if (!outKeyFile.equals(""))
					genkey(true, outKeyFile);
			}
		});

		GroupLayout jPanel7Layout = new GroupLayout(cryptParamPanel);
		cryptParamPanel.setLayout(jPanel7Layout);
		jPanel7Layout.setHorizontalGroup(
				jPanel7Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
						.addGap(0, 0, Short.MAX_VALUE)
						.addComponent(genKeys))
						.addGroup(jPanel7Layout.createSequentialGroup()
								.addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
										.addComponent(modLabel)
										.addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
														.addComponent(priKeyLabel)
														.addComponent(keyFileLabel))
														.addComponent(pubKeyLabel, GroupLayout.Alignment.TRAILING)))
														.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
														.addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
																.addComponent(pubKeyValue)
																.addGroup(jPanel7Layout.createSequentialGroup()
																		.addComponent(keyFilePath)
																		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(keyFilePathSelButton, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE))
																		.addComponent(priKayValue)
																		.addComponent(modValue)))
		);
		jPanel7Layout.setVerticalGroup(
				jPanel7Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(jPanel7Layout.createSequentialGroup()
						.addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(keyFileLabel)
								.addComponent(keyFilePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(keyFilePathSelButton))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(priKayValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(priKeyLabel))
										.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(pubKeyValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(pubKeyLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
												.addGap(13, 13, 13)
												.addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
														.addComponent(modValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
														.addComponent(modLabel))
														.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
														.addComponent(genKeys))
		);

		GroupLayout jPanel1Layout = new GroupLayout(optionsPanel);
		optionsPanel.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(
				jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(cryptParamPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(optionalParamPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
						.addGap(14, 14, 14)
						.addComponent(hideRadiButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(rtrvRadioButton)
						.addGap(17, 17, 17))
						.addComponent(fileSelectionPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		jPanel1Layout.setVerticalGroup(
				jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup()
						.addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(hideRadiButton)
								.addComponent(rtrvRadioButton))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(fileSelectionPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(optionalParamPanel, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(cryptParamPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addContainerGap())
		);

		inputPanel.setBorder(BorderFactory.createTitledBorder("Input"));
		inputImageScrollPanel.setViewportView(inputImage);

		GroupLayout jPanel2Layout = new GroupLayout(inputPanel);
		inputPanel.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(
				jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(inputImageScrollPanel, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
		);
		jPanel2Layout.setVerticalGroup(
				jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(inputImageScrollPanel, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
		);

		ouputPanel.setBorder(BorderFactory.createTitledBorder("Output"));
		outTextScrollPanel.setViewportView(outputImage);

		outTextArea.setColumns(20);
		outTextArea.setRows(5);

		GroupLayout jPanel3Layout = new GroupLayout(ouputPanel);
		ouputPanel.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(
				jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(outTextScrollPanel, GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
		);
		jPanel3Layout.setVerticalGroup(
				jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(outTextScrollPanel, GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
		);

		stegAction.setText("Hide/Unhide");
		stegAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (hideRadiButton.isSelected())
					isHide = true;
				else if (rtrvRadioButton.isSelected())
					isHide = false;

				if (encryptCheckBox.isSelected())
				{
					isEncrypt = true;

				}
				else
					isEncrypt = false;

				if (compressCheckBox.isSelected())
					isCompress = true;
				else
					isCompress = false;

				hideRetriveAction(isHide, isEncrypt, isCompress);
			}
		});

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
										.addComponent(optionsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addComponent(ouputPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(inputPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
												.addGroup(layout.createSequentialGroup()
														.addComponent(statusBar)
														.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
														.addComponent(stegAction)
														.addGap(34, 34, 34))))
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
								.addGroup(layout.createSequentialGroup()
										.addComponent(inputPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(ouputPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addGap(5, 5, 5))
										.addComponent(optionsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(stegAction)
												.addComponent(statusBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
												.addGap(46, 46, 46))
		);
	}

	protected void genkey(boolean b, String outKeyFile) {
		Test test = new Test();
		int bitlen = 512;

		KeyValues keyVal = test.Keys(bitlen);

		pubKeyValue.setText(keyVal.pubkey.toString());
		priKayValue.setText(keyVal.privkey.toString());
		modValue.setText(keyVal.mod.toString());  
		keyFilePath.setText(outKeyFile);

		try
		{ 
			ObjectOutputStream keyObjectOutStream =new ObjectOutputStream(new FileOutputStream(outKeyFile));
			keyObjectOutStream.writeObject((KeyValues)keyVal);
			keyObjectOutStream.close();
		}catch(Exception e){
			System.out.println("Error: "+ e);} 		
	}

	private void setInitialValues(boolean isHideIn)
	{
		if (isHideIn)
			hideRadiButton.setSelected(true);

		inImagePath.setText("");
		inDataPath.setText("");
		outDataPath.setText("");
		outTextArea.setText("");
		setStatusMessage("");
		inputImage.setIcon(null);
		outputImage.setIcon(null);
		compressCheckBox.setEnabled(true);
		compressCheckBox.setSelected(false);
		encryptCheckBox.setSelected(false);
		DisableAllEncryptionParameters(true);
	}

	private void DisableAllEncryptionParameters(boolean valueIn) {
		if (valueIn == true)
		{
			keyFilePath.setText("");
			priKayValue.setText("");
			pubKeyValue.setText("");
			modValue.setText("");
			keyFilePath.setEnabled(false);
			keyFilePathSelButton.setEnabled(false);
			priKayValue.setEnabled(false);
			pubKeyValue.setEnabled(false);
			modValue.setEnabled(false);
			genKeys.setEnabled(false);
		}
		else
		{
			keyFilePath.setEnabled(true);
			keyFilePathSelButton.setEnabled(true);
			priKayValue.setEnabled(true);
			pubKeyValue.setEnabled(true);
			modValue.setEnabled(true);
			if (!rtrvRadioButton.isSelected())
				genKeys.setEnabled(true);
		}
	}

	private boolean selectFileAction(String titleTextIn, JTextField fileLocIn, boolean allowFileFilter, 
			boolean setOutFile)
	{
		boolean isOkSelected = false;
		JFileChooser chooser = new JFileChooser();
		if (allowFileFilter)
		{
			FileNameExtensionFilter filter = new FileNameExtensionFilter("GIF Images", "gif");
			chooser.setFileFilter(filter);
		}
		if (setOutFile)
		{
			if (hideRadiButton.isSelected())
			{
				if (titleTextIn.contains("Key"))
					chooser.setSelectedFile(new File("enckey"));
				else
					chooser.setSelectedFile(new File("output.gif"));
			}
			else
				chooser.setSelectedFile(new File("output.txt"));
		}
		chooser.setDialogTitle(titleTextIn);
		int returnVal = chooser.showOpenDialog(getContentPane());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			isOkSelected = true;
			fileLocIn.setText(chooser.getSelectedFile().getAbsolutePath());
		}
		
		return isOkSelected;
	}

	private void hideRetriveAction(boolean isHideIn, boolean isEncryptIn, boolean isCompressIn) {
		System.out.println("Enter Steg Action");
		String inImageFileName = inImagePath.getText();
		String inDataFileName = inDataPath.getText();
		String outDataFileName = outDataPath.getText();

		if (!(inImageFileName.endsWith(".GIF") || inImageFileName.endsWith(".gif"))){
			JOptionPane.showMessageDialog(null,
					"Please select a valid GIF file for Input Image",
					"Error:",
					JOptionPane.ERROR_MESSAGE);
			setStatusMessage("STEG ACTION FAILED.");
			return;
		}

		if (isHideIn)
		{
			if (!(inDataFileName.endsWith(".txt") || inDataFileName.endsWith(".TXT"))){
				JOptionPane.showMessageDialog(null,
						"Please select a valid Text file to hide",
						"Error:",
						JOptionPane.ERROR_MESSAGE);
				setStatusMessage("STEG ACTION FAILED.");
				return;
			}	

			if (!(outDataFileName.endsWith(".GIF") || outDataFileName.endsWith(".gif"))){
				JOptionPane.showMessageDialog(null,
						"Please set a valid GIF file to store Output",
						"Error:",
						JOptionPane.ERROR_MESSAGE);
				setStatusMessage("STEG ACTION FAILED.");
				return;
			}

			BigInteger modValue 	= null;
			BigInteger publicKey 	= null;
			BigInteger privateKey 	= null;

			FileInputStream 	inDataStream 	= null;	
			FileOutputStream 	outImageStream 	= null;
			StegoEncoder 		encodedImage 	= null;

			// Creating fileStream for the selected files
			try {
				inDataStream	= new FileInputStream(new File(inDataFileName));
				outImageStream 	= new FileOutputStream(new File(outDataFileName));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Error: Loading File Please select valid file.",
						"Error:",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			long inImageSize 	= new File(inImageFileName).length();
			long inDataSize		= new File(inDataFileName).length();
			long availSpace		= inImageSize/8;

			if (availSpace != 0 && inDataSize > availSpace)
			{
				JOptionPane.showMessageDialog(null,
						"The total available space to hide is "+availSpace+ " bytes.\n The selected Input file"+
						"size is "+ inDataSize + " bytes. This will cause the problem in Hide/Retrive.",
						"Warning:",
						JOptionPane.WARNING_MESSAGE);
				setStatusMessage("STEG ACTION FAILED.");
				return;
			}

			//Encrypting Input Data
			if (isEncryptIn){
				setStatusMessage("Encryption action InProgress.");
				KeyValues keys = null;
				String keyFile = keyFilePath.getText();
				if (keyFile.isEmpty())
				{
					JOptionPane.showMessageDialog(null,
							"Please select a valid Key file",
							"Error:",
							JOptionPane.ERROR_MESSAGE);
					return;
				}      
				try
				{ 
					InputStream fis=new FileInputStream(keyFile);
					ObjectInputStream ois =new ObjectInputStream(fis);
					keys=(KeyValues)ois.readObject();
				}catch(Exception rer){
					JOptionPane.showMessageDialog(null,
							"Please select a valid Key file",
							"Error:",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (keys instanceof KeyValues)
				{
					modValue 	= keys.mod;
					publicKey 	= keys.pubkey;
					privateKey 	= keys.privkey;
				}

				Cryptography encryption =new Cryptography();
				PublicKey pubKey = new PublicKey(modValue, publicKey);
				PrivateKey priKey = new PrivateKey(modValue, privateKey);

				inDataStream = (FileInputStream) encryption.doencryptcomp(inDataStream, pubKey, priKey);
				setStatusMessage("Encryption Action Completed.");
				System.out.println("Encryption Completed");
			}

			//Compressing input data
			if (isCompressIn) {
				setStatusMessage("Compression Action Inprogress.");
				Compression compresser = new Compression();
				inDataStream = (FileInputStream) compresser.docomp(inDataStream);
				setStatusMessage("Compression Action completed.");
				System.out.println("Compression completed");
			}

			//Creating Encoded GIF Image to hide the data
			try {
				setStatusMessage("Hide Action Inprogress.");
				Image inputImage = getContentPane().createImage(new FileImageSource(inImageFileName));
				loadImage(inputImage);
				encodedImage = new StegoEncoder( inputImage, outImageStream, null, rgbPalette);
				encodedImage.setFunction(StegoEncoder.STEG);
				encodedImage.setInputStream(inDataStream);
				encodedImage.encode();
				outImageStream.close();
				BufferedImage tempPicture = null;
				try {
					tempPicture = ImageIO.read(new File(outDataPath.getText()));
					ImageIcon tempIcon = new ImageIcon( tempPicture );
					outputImage.setIcon(tempIcon);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							"Unable to Display Ouput Image.",
							"Information:",
							JOptionPane.INFORMATION_MESSAGE);
				}
				setStatusMessage("Hide Action Completed.");
			} catch (Exception e)
			{
				JOptionPane.showMessageDialog(null,
						"Encoding Error: Not able to encode the GIF Image./n Please select a valid GIF file.",
						"Error:",
						JOptionPane.ERROR_MESSAGE);
				setStatusMessage("STEG ACTION FAILED.");
				return;
			}
			System.out.println("Exit Steg Action");
		}
		else {
			System.out.println("Enter unsteg Action");
			if (!(outDataFileName.endsWith(".txt") || outDataFileName.endsWith(".TXT"))){
				JOptionPane.showMessageDialog(null,
						"Please set a valid text file to store retrieved data",
						"Error:",
						JOptionPane.ERROR_MESSAGE);
				setStatusMessage("STEG ACTION FAILED.");
				return;
			}

			OutputStream 	outDataStream 	= null;
			StegoEncoder 	encodedImage 	= null;

			try {
				outDataStream 	= new FileOutputStream(new File(outDataFileName));
			} catch (FileNotFoundException e) {
				setStatusMessage("STEG ACTION FAILED.");
				e.printStackTrace();
			}

			try {
				Image inputImage = getContentPane().createImage(new FileImageSource(inImageFileName));
				loadImage(inputImage);
				encodedImage = new StegoEncoder(inputImage, outDataStream, null, rgbPalette);
				encodedImage.setFunction(StegoEncoder.UNSTEG);
				encodedImage.encode();
				outDataStream.close();
				System.out.println("Output image decoded");
			} catch (Exception e)
			{
				e.printStackTrace();
			}

			try
			{
				FileInputStream outStream 	= new FileInputStream(new File(outDataFileName));

				if (isCompressIn){
					Decompression decompresser = new Decompression();
					outStream = (FileInputStream) decompresser.dodecomp(outStream);
					System.out.println("Output image decompressed");
				}

				if (isEncryptIn){
					KeyValues keys = null;
					try
					{ 
						InputStream 		keyInputStream 	= new FileInputStream(keyFilePath.getText());
						ObjectInputStream 	keyObjectStream = new ObjectInputStream(keyInputStream);
						keys = (KeyValues)keyObjectStream.readObject();
					}catch(Exception rer){
						JOptionPane.showMessageDialog(null,
								"Error reading the key file. \nPlease set a valid Key file.",
								"Error:",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					BigInteger modValue 	= keys.mod;
					BigInteger publicKey 	= keys.pubkey;
					BigInteger privateKey 	= keys.privkey;

					Cryptography decryption = new Cryptography();
					PublicKey pubKey = new PublicKey(modValue, publicKey);
					PrivateKey priKey = new PrivateKey(modValue, privateKey);
					outStream = (FileInputStream) decryption.dodecryptcomp(outStream, pubKey, priKey);
					System.out.println("Output image decrypted");
				}

				byte rtr[]=new byte[outStream.available()];
				outStream.read(rtr);
				outTextArea.setText(new String(rtr));
				File file = new File(outDataFileName);
				Writer outImageStream2 	= new BufferedWriter(new FileWriter(file));
				outImageStream2.write(outTextArea.getText());
				outImageStream2.close();
				setStatusMessage("Retrive Action Completed.");
			}catch(Exception e4rr){}
			System.out.println("Exit unsteg Action");
		}
	}

	public void setStatusMessage(String statusIn){
		statusBar.setText(statusIn);
	}

	public Image loadImage(Image inputImage)
	{
		System.out.println("CALLED EZSTEGO loadImage()");
		try
		{
			boolean resi = waitForImage(getContentPane(), inputImage);
			if(!resi)
			{
				return null;
			}
		}
		catch (Throwable e)
		{
			return null;
		}

		getImageStats(inputImage);

		System.out.println("END EZSTEGO loadImage()");
		return inputImage;
	}

	public void getImageStats(Image theImage)
	{
		System.out.println("CALLED EZSTEGO getImageStats()");
		try
		{
			imagePro = theImage.getSource();
			imagePro.addConsumer(this);
			imagePro.startProduction(this);
		}
		catch (Throwable e)
		{
			return;
		}
		while (numRGBvalues == 0) {};
		System.out.println("END EZSTEGO getImageStats()");
	}

	boolean waitForImage(Component component, Image myImage)
	{
		System.out.println("CALLED EZSTEGO waitForImage()");
		MediaTracker tracker = new MediaTracker(component);
		boolean getError = false;
		try
		{
			tracker.addImage(myImage, 0);
			tracker.waitForID(0);
			getError = tracker.isErrorAny();
		} 
		catch (Throwable anyDamnThing)
		{
			return false;
		}
		if (getError) 
		{
			return false;
		}
		System.out.println("END EZSTEGO waitForImage()");
		return true;
	}

	public void imageComplete (int status) {
		System.out.println("CALLED EZSTEGO imageComplete()");
		imagePro.removeConsumer(this);
		System.out.println("END EZSTEGO imageComplete()");
	}

	public void setColorModel (ColorModel model) {
		System.out.println("CALLED EZSTEGO setColorModel()");
		if (model instanceof IndexColorModel) {
			// Print contents of IndexColorTable
			IndexColorModel im = (IndexColorModel) model;
			numRGBvalues = im.getMapSize();

			rgbPalette = new int[numRGBvalues];
			for (int ii = 0; ii < numRGBvalues; ii++) {
				rgbPalette[ii] = im.getRGB(ii);
			} 
		}

		System.out.println("END EZSTEGO setColorModel()");
	}

	public void setDimensions(int arg0, int arg1) {

	}

	public void setHints(int arg0) {

	}

	public void setPixels(int arg0, int arg1, int arg2, int arg3,
			ColorModel arg4, byte[] arg5, int arg6, int arg7) {

	}

	public void setPixels(int arg0, int arg1, int arg2, int arg3,
			ColorModel arg4, int[] arg5, int arg6, int arg7) {

	}

	public void setProperties(Hashtable<?, ?> arg0) {

	}
}
