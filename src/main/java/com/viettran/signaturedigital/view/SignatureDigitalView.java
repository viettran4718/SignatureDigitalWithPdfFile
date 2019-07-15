package com.viettran.signaturedigital.view;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import com.viettran.signaturedigital.model.SignDocument;

public class SignatureDigitalView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static JPanel contentPane;
	private static JTextField txtAddress;
	private static JTextField txtReason;
	private static JTextField txtSelectedFile;
	private static JTextField txtSaveFolder;
	private static JComboBox<String> cmbAlias;
	private static JButton button_2;
	private static JButton button_3;
	private static JButton btnSelectSaveFolder;
	private static JButton btnSelectFile;

	private static String fileName = "";

	private static SignDocument sd;
	private JMenuBar menuBar;
	private JMenu mnNewMenu;
	private JMenuItem mntmNewMenuItem;
	private JMenuItem mntmAbout;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SignatureDigitalView frame = new SignatureDigitalView();
					frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SignatureDigitalView() {
		setTitle("Phần mềm ký số - B&T Company");
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int i = JOptionPane.showConfirmDialog(null, "Bạn có muốn thoát", "Xác nhận", JOptionPane.YES_NO_OPTION);
				if (i == JOptionPane.YES_OPTION)
					System.exit(0);
			}
		});
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(400, 300);
		setLocationRelativeTo(null);

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mnNewMenu = new JMenu("Tùy chọn");
		menuBar.add(mnNewMenu);

		mntmNewMenuItem = new JMenuItem("Làm mới");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadAliasToCombobox();
				clearText();
			}
		});
		mnNewMenu.add(mntmNewMenuItem);

		mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JLabel label = new JLabel("<html><center>Phần mềm xác ký văn bản pdf<br>Phiên bản: 1.0<br>Tác giả: Việt Trần <br> Liên hệ: viettran4718@gmail.com");
				label.setHorizontalAlignment(SwingConstants.CENTER);
				JOptionPane.showMessageDialog(null, label,"Thông tin phần mềm",JOptionPane.PLAIN_MESSAGE);
				
			}
		});
		mnNewMenu.add(mntmAbout);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel mainPanel = new JPanel();
		contentPane.add(mainPanel, BorderLayout.CENTER);
		GridBagLayout gbl_mainPanel = new GridBagLayout();
		gbl_mainPanel.columnWidths = new int[] { 180, 220, 0 };
		gbl_mainPanel.rowHeights = new int[] { 31, 31, 31, 31, 31, 31, 31, 0 };
		gbl_mainPanel.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gbl_mainPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		mainPanel.setLayout(gbl_mainPanel);

		JLabel label_1 = new JLabel("Cá nhân/ Tổ chức");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.anchor = GridBagConstraints.EAST;
		gbc_label_1.fill = GridBagConstraints.VERTICAL;
		gbc_label_1.insets = new Insets(2, 2, 2, 2);
		gbc_label_1.gridx = 0;
		gbc_label_1.gridy = 0;
		mainPanel.add(label_1, gbc_label_1);

		cmbAlias = new JComboBox<String>();
		cmbAlias.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				try {
					sd.setupCertificate(cmbAlias.getSelectedItem().toString());
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
//		cmbAlias.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				sd.setupCertificate(cmbAlias.getSelectedItem().toString());
//			}
//		});
//		cmbAlias.addItemListener(new ItemListener() {
//			public void itemStateChanged(ItemEvent arg0) {
//				
//			}
//		});
		GridBagConstraints gbc_cmbAlias = new GridBagConstraints();
		gbc_cmbAlias.fill = GridBagConstraints.BOTH;
		gbc_cmbAlias.insets = new Insets(2, 2, 2, 2);
		gbc_cmbAlias.gridx = 1;
		gbc_cmbAlias.gridy = 0;
		mainPanel.add(cmbAlias, gbc_cmbAlias);

		JLabel label_2 = new JLabel("Địa điểm");
		GridBagConstraints gbc_label_2 = new GridBagConstraints();
		gbc_label_2.anchor = GridBagConstraints.EAST;
		gbc_label_2.fill = GridBagConstraints.VERTICAL;
		gbc_label_2.insets = new Insets(2, 2, 2, 2);
		gbc_label_2.gridx = 0;
		gbc_label_2.gridy = 1;
		mainPanel.add(label_2, gbc_label_2);

		btnSelectFile = new JButton("Chọn file ký");
		btnSelectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Chọn file muốn ký");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				//
				// disable the "All files" option.
				//
				chooser.setAcceptAllFileFilterUsed(false);

				chooser.addChoosableFileFilter(new FileFilter() {

					@Override
					public String getDescription() {
						// TODO Auto-generated method stub
						return "PDF Documents (*.pdf)";
					}

					@Override
					public boolean accept(File f) {
						// TODO Auto-generated method stub
						if (f.isDirectory()) {
							return true;
						} else {
							return f.getName().toLowerCase().endsWith(".pdf");
						}
					}
				});
				if (chooser.showOpenDialog(contentPane) == JFileChooser.APPROVE_OPTION) {
					txtSelectedFile.setText(chooser.getSelectedFile().toString());
					File file = chooser.getSelectedFile();
					txtSaveFolder.setText("<Vui lòng chọn folder lưu>");
					txtSaveFolder.setEditable(false);
					fileName = file.getName();
				}
			}
		});

		txtAddress = new JTextField();
		txtAddress.setColumns(10);
		GridBagConstraints gbc_txtAddress = new GridBagConstraints();
		gbc_txtAddress.fill = GridBagConstraints.BOTH;
		gbc_txtAddress.insets = new Insets(2, 2, 2, 2);
		gbc_txtAddress.gridx = 1;
		gbc_txtAddress.gridy = 1;
		mainPanel.add(txtAddress, gbc_txtAddress);

		JLabel label_3 = new JLabel("Lý do");
		GridBagConstraints gbc_label_3 = new GridBagConstraints();
		gbc_label_3.anchor = GridBagConstraints.EAST;
		gbc_label_3.fill = GridBagConstraints.VERTICAL;
		gbc_label_3.insets = new Insets(2, 2, 2, 2);
		gbc_label_3.gridx = 0;
		gbc_label_3.gridy = 2;
		mainPanel.add(label_3, gbc_label_3);

		txtReason = new JTextField();
		txtReason.setColumns(10);
		GridBagConstraints gbc_txtReason = new GridBagConstraints();
		gbc_txtReason.fill = GridBagConstraints.BOTH;
		gbc_txtReason.insets = new Insets(2, 2, 2, 2);
		gbc_txtReason.gridx = 1;
		gbc_txtReason.gridy = 2;
		mainPanel.add(txtReason, gbc_txtReason);
		btnSelectFile.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_btnSelectFile = new GridBagConstraints();
		gbc_btnSelectFile.anchor = GridBagConstraints.EAST;
		gbc_btnSelectFile.fill = GridBagConstraints.VERTICAL;
		gbc_btnSelectFile.insets = new Insets(2, 2, 2, 2);
		gbc_btnSelectFile.gridx = 0;
		gbc_btnSelectFile.gridy = 3;
		mainPanel.add(btnSelectFile, gbc_btnSelectFile);

		txtSelectedFile = new JTextField();
		txtSelectedFile.setColumns(10);
		GridBagConstraints gbc_txtSelectedFile = new GridBagConstraints();
		gbc_txtSelectedFile.fill = GridBagConstraints.BOTH;
		gbc_txtSelectedFile.insets = new Insets(2, 2, 2, 2);
		gbc_txtSelectedFile.gridx = 1;
		gbc_txtSelectedFile.gridy = 3;
		mainPanel.add(txtSelectedFile, gbc_txtSelectedFile);

		btnSelectSaveFolder = new JButton("Chọn file lưu");
		btnSelectSaveFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!fileName.equals("")) {

					JFileChooser chooser = new JFileChooser();
					chooser.setDialogTitle("Chọn thư mục lưu");
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					//
					// disable the "All files" option.
					//
					chooser.setAcceptAllFileFilterUsed(false);
					//
					if (chooser.showOpenDialog(contentPane) == JFileChooser.APPROVE_OPTION) {

						txtSaveFolder.setText(chooser.getSelectedFile().toString() + "\\sign_" + fileName);
						txtSaveFolder.setEditable(true);

					}
				} else
					JOptionPane.showMessageDialog(null, "Xin mời chọn file muốn ký trước");
			}

		});
		btnSelectSaveFolder.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_btnSelectSaveFolder = new GridBagConstraints();
		gbc_btnSelectSaveFolder.anchor = GridBagConstraints.EAST;
		gbc_btnSelectSaveFolder.fill = GridBagConstraints.VERTICAL;
		gbc_btnSelectSaveFolder.insets = new Insets(2, 2, 2, 2);
		gbc_btnSelectSaveFolder.gridx = 0;
		gbc_btnSelectSaveFolder.gridy = 4;
		mainPanel.add(btnSelectSaveFolder, gbc_btnSelectSaveFolder);

		txtSaveFolder = new JTextField();
		txtSaveFolder.setColumns(10);
		GridBagConstraints gbc_txtSaveFolder = new GridBagConstraints();
		gbc_txtSaveFolder.anchor = GridBagConstraints.NORTH;
		gbc_txtSaveFolder.fill = GridBagConstraints.BOTH;
		gbc_txtSaveFolder.insets = new Insets(2, 2, 2, 2);
		gbc_txtSaveFolder.gridx = 1;
		gbc_txtSaveFolder.gridy = 4;
		mainPanel.add(txtSaveFolder, gbc_txtSaveFolder);

		JLabel label = new JLabel("");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.fill = GridBagConstraints.BOTH;
		gbc_label.insets = new Insets(2, 2, 2, 2);
		gbc_label.gridx = 0;
		gbc_label.gridy = 5;
		mainPanel.add(label, gbc_label);

		button_2 = new JButton("Tiến hành ký");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sign();
			}
		});
		GridBagConstraints gbc_button_2 = new GridBagConstraints();
		gbc_button_2.fill = GridBagConstraints.VERTICAL;
		gbc_button_2.insets = new Insets(2, 2, 2, 2);
		gbc_button_2.gridx = 1;
		gbc_button_2.gridy = 5;
		mainPanel.add(button_2, gbc_button_2);

		JLabel label_4 = new JLabel("");
		GridBagConstraints gbc_label_4 = new GridBagConstraints();
		gbc_label_4.fill = GridBagConstraints.BOTH;
		gbc_label_4.insets = new Insets(2, 2, 2, 2);
		gbc_label_4.gridx = 0;
		gbc_label_4.gridy = 6;
		mainPanel.add(label_4, gbc_label_4);

		button_3 = new JButton("Hủy bỏ");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clearText();
			}
		});
		GridBagConstraints gbc_button_3 = new GridBagConstraints();
		gbc_button_3.fill = GridBagConstraints.VERTICAL;
		gbc_button_3.gridx = 1;
		gbc_button_3.gridy = 6;
		mainPanel.add(button_3, gbc_button_3);

		JPanel topPanel = new JPanel();
		contentPane.add(topPanel, BorderLayout.NORTH);

		JPanel bottomPanel = new JPanel();
		contentPane.add(bottomPanel, BorderLayout.SOUTH);

		loadAliasToCombobox();
		clearText();

	}

	private static void loadAliasToCombobox() {
		cmbAlias.removeAllItems();
		sd = new SignDocument();
		for (String alias : sd.getListAlias()) {
			cmbAlias.addItem(alias);
		}
		String alias = cmbAlias.getItemAt(0);
		sd.setupCertificate(alias);
	}

	private static void sign() {
		if (isValidField()) {
			sd.signPdf(cmbAlias.getSelectedItem().toString(), txtSelectedFile.getText(), txtSaveFolder.getText(),
					txtReason.getText(), txtAddress.getText());
		}
	}

	private static void clearText() {
		txtAddress.setText("");
		txtReason.setText("");
		txtSelectedFile.setText("<Vui lòng chọn file pdf muốn ký>");
		txtSelectedFile.setEditable(false);
		txtSaveFolder.setText("<Vui lòng chọn thư mục lưu file ký>");
		txtSaveFolder.setEditable(false);
		fileName = "";
	}

	private static boolean isValidField() {
		if (txtAddress.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(null, "Mời nhập địa điểm ký");
			txtAddress.requestFocus();
			return false;
		}
		if (txtReason.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(null, "Mời nhập lý do ký");
			txtReason.requestFocus();
			return false;
		}
		if (txtSelectedFile.getText().equals("")
				|| txtSelectedFile.getText().equals("<Vui lòng chọn file pdf muốn ký>")) {
			JOptionPane.showMessageDialog(null, "Mời chọn file pdf muốn ký");
			btnSelectFile.requestFocus();
			return false;
		}
		if (txtSaveFolder.getText().trim().equals("")
				|| txtSaveFolder.getText().equals("<Vui lòng chọn thư mục lưu file ký>")) {
			JOptionPane.showMessageDialog(null, "Mời chọn thư mục lưu file ký");
			btnSelectSaveFolder.requestFocus();
			return false;
		}

		return true;
	}

}
