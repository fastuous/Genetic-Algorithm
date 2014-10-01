/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trianglegenome.gui;

import javax.swing.GroupLayout;

import trianglegenome.util.Constants;

/**
 *
 * @author masonbanning
 */
public class MainFrame extends javax.swing.JFrame
{
  private static final long serialVersionUID = 1L;
  
  private GUIController controller;
  
  private javax.swing.JButton btnPause;
  private javax.swing.JButton btnNext;
  private javax.swing.JButton btnGenomeTable;
  private javax.swing.JButton btnReadGenome;
  private javax.swing.JButton btnWriteGenome;
  private javax.swing.JButton btnStatsFile;
  private javax.swing.JComboBox<String> comboSelectImage;
  private javax.swing.JComboBox<String> jComboBox2;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel4;
  private ImagePanel imagePanel;
  private DrawPanel drawPanel;
  private javax.swing.JSlider triangleSlider;
  private javax.swing.JSlider tribeSlider;
  private javax.swing.JTextField jTextField1;

  /**
   * Creates new form NewJFrame
   * @param controller 
   */
  public MainFrame(GUIController controller)
  {
    super("Image Evolver");
    /* Set the Nimbus look and feel */
    // <editor-fold defaultstate="collapsed"
    // desc=" Look and feel setting code (optional) ">
    /*
     * If Nimbus (introduced in Java SE 6) is not available, stay with the
     * default look and feel. For details see
     * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
     */
    try
    {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
          .getInstalledLookAndFeels())
      {
        if ("Nimbus".equals(info.getName()))
        {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    }
    catch (ClassNotFoundException ex)
    {
      java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(
          java.util.logging.Level.SEVERE, null, ex);
    }
    catch (InstantiationException ex)
    {
      java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(
          java.util.logging.Level.SEVERE, null, ex);
    }
    catch (IllegalAccessException ex)
    {
      java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(
          java.util.logging.Level.SEVERE, null, ex);
    }
    catch (javax.swing.UnsupportedLookAndFeelException ex)
    {
      java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(
          java.util.logging.Level.SEVERE, null, ex);
    }
    // </editor-fold>
    /* Create and display the form */
    this.controller = controller;
    initComponents();
    setResizable(false);
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed"
  // desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents()
  {
    btnPause = new javax.swing.JButton("Start");
    btnNext = new javax.swing.JButton("Next");
    btnGenomeTable = new javax.swing.JButton("Show Genome Table");
    btnReadGenome = new javax.swing.JButton("Read Genome");
    btnWriteGenome = new javax.swing.JButton("Write Genome");
    btnStatsFile = new javax.swing.JButton("Append Stats File");
    comboSelectImage = new javax.swing.JComboBox<String>();
    jComboBox2 = new javax.swing.JComboBox<String>();
    jLabel1 = new javax.swing.JLabel("Label 1");
    jLabel2 = new javax.swing.JLabel("Label 2");
    jLabel3 = new javax.swing.JLabel("Label 3");
    jLabel4 = new javax.swing.JLabel("Fitness:                   ");
    jLabel5 = new javax.swing.JLabel("Triangle 200/200 ");
    imagePanel = new ImagePanel(512, 413);
    drawPanel = new DrawPanel(512, 413);
    triangleSlider = new javax.swing.JSlider(0, Constants.TRIANGLE_COUNT, Constants.TRIANGLE_COUNT);
    triangleSlider.setValue(200);
    tribeSlider = new javax.swing.JSlider();
    jTextField1 = new javax.swing.JTextField();
    controller.setDrawPanel(drawPanel);
    controller.setImagePanel(imagePanel);
    imagePanel.selectImage("mona-lisa-cropped-512x413.png");
    drawPanel.setTriangleDrawLimit(200);
    
    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    imagePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(imagePanel);
    imagePanel.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(
        javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 512, Short.MAX_VALUE));
    jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(
        javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
    drawPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(drawPanel);
    drawPanel.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
        javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 512, Short.MAX_VALUE));
    jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(
        javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 510, Short.MAX_VALUE));
    
    btnGenomeTable.addActionListener(e -> controller.genomeTable());
    btnPause.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
          
        controller.pause();

        if(controller.isPaused)
        {
          btnPause.setText("Start");
          btnNext.setEnabled(true);
          btnGenomeTable.setEnabled(true);
          btnReadGenome.setEnabled(true);
          btnWriteGenome.setEnabled(true);
          btnStatsFile.setEnabled(true);
          comboSelectImage.setEnabled(true);
          triangleSlider.setEnabled(true);
        }
        else
        {
          btnPause.setText("Pause");
          btnNext.setEnabled(false);
          btnGenomeTable.setEnabled(false);
          btnReadGenome.setEnabled(false);
          btnWriteGenome.setEnabled(false);
          btnStatsFile.setEnabled(false);
          comboSelectImage.setEnabled(false);
          triangleSlider.setEnabled(false);
        }
      }
    });
    btnNext.addActionListener(e -> controller.next());
    btnReadGenome.addActionListener(e -> controller.readGenome());
    btnWriteGenome.addActionListener(e -> controller.writeGenome());
    btnStatsFile.addActionListener(e -> controller.appendStats());
    
    
    triangleSlider.addChangeListener(e -> controller.triangleSliderUpdate(triangleSlider.getValue()));
    
    comboSelectImage.setModel(new javax.swing.DefaultComboBoxModel<String>(Constants.IMAGE_FILES));
    comboSelectImage.addActionListener(e -> {
      imagePanel.selectImage(comboSelectImage.getSelectedItem().toString());
      imagePanel.setSize(Constants.width,Constants.height);
      drawPanel.setSize(Constants.width,Constants.height);
      controller.setDrawPanel(drawPanel);
      controller.setImagePanel(imagePanel);
      }
  );

    jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<String>(new String[]
    {"Item 1", "Item 2", "Item 3", "Item 4"}));
    jTextField1.setText("jTextField1");
    jTextField1.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        //TODO Add action code for text field
      }
    });
    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout
        .setHorizontalGroup(layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                layout
                    .createSequentialGroup()
                    .addGroup(
                        layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tribeSlider, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(
                                layout
                                    .createSequentialGroup()
                                    .addGroup(
                                        layout
                                            .createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(
                                                layout
                                                    .createSequentialGroup()
                                                    .addContainerGap()
                                                    .addGroup(
                                                        layout
                                                            .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                                            .addComponent(
                                                                imagePanel,
                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(
                                                                comboSelectImage,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                512,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addGroup(
                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                layout
                                                    .createSequentialGroup()
                                                    .addGroup(
                                                        layout
                                                            .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                false)
                                                            .addGroup(
                                                                layout
                                                                    .createSequentialGroup()
                                                                    .addComponent(
                                                                        btnPause,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        170,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                    .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addComponent(
                                                                        btnNext,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        170,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                                            .addGroup(
                                                                layout
                                                                    .createSequentialGroup()
                                                                    .addContainerGap()
                                                                    .addComponent(
                                                                        jLabel1,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        156,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                    .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        Short.MAX_VALUE)
                                                                    .addComponent(
                                                                        jLabel2,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        166,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                    .addPreferredGap(
                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(
                                                        layout
                                                            .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                false)
                                                            .addGroup(
                                                                layout
                                                                    .createSequentialGroup()
                                                                    .addGap(6, 6, 6)
                                                                    .addComponent(
                                                                        jLabel3,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        Short.MAX_VALUE))
                                                            .addComponent(
                                                                btnGenomeTable,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                166,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(
                                        layout
                                            .createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(
                                                layout
                                                    .createSequentialGroup()
                                                    .addGroup(
                                                        layout
                                                            .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                false)
                                                            .addComponent(
                                                                jComboBox2,
                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                0,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE)
                                                            .addGroup(
                                                                layout
                                                                    .createSequentialGroup()
                                                                    .addComponent(
                                                                        btnReadGenome,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        170,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                    .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addComponent(
                                                                        btnWriteGenome,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        170,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                    .addPreferredGap(
                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(
                                                        layout
                                                            .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(
                                                                btnStatsFile,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE)
                                                            .addComponent(jTextField1)))
                                            .addGroup(
                                                layout
                                                    .createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(
                                                        layout
                                                            .createSequentialGroup()
                                                            .addComponent(jLabel5, GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                                            .addComponent(
                                                                triangleSlider,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addPreferredGap(
                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(
                                                                jLabel4,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE))
                                                    .addComponent(drawPanel,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addContainerGap()));
    layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(
            layout
                .createSequentialGroup()
                .addContainerGap()
                .addGroup(
                    layout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(drawPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(imagePanel, javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(
                    layout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(comboSelectImage, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5, GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                        .addComponent(triangleSlider, javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tribeSlider, javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7,
                    Short.MAX_VALUE)
                .addGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnPause).addComponent(btnNext).addComponent(btnGenomeTable)
                        .addComponent(btnReadGenome).addComponent(btnWriteGenome).addComponent(btnStatsFile))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(
                    layout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jLabel1)
                        .addComponent(jLabel2)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE))));
    pack();
  }// </editor-fold>//GEN-END:initComponents

  public void setDisplayFitness(int fitness)
  {
    jLabel4.setText("Fitness: " + fitness);
  }
  public void setTriangleLabel(int value)
  {
    jLabel5.setText("Triangle: " + value + "/200 ");
  }
  // Variables declaration - do not modify//GEN-BEGIN:variables
}