import javax.sound.midi.Instrument;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import java.awt.event.*;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import java.io.*;


public class ViewMidiPlayer implements Runnable {
	private JButton botaoAbrir = new JButton("Abrir arquivo midi ...");
	private JButton botaoPlay = new JButton("\u25b6");
	private JButton botaoPause = new JButton("\u25ae\u25ae");
	private JButton botaoStop = new JButton("\u25fc");
	private JButton botaoBanco = new JButton("Soundfonts");
	private JButton botaoInstrumentos = new JButton("Mostrar Instrumentos");
	
	private JPanel painel1 = new JPanel();
	private JPanel painel2 = new JPanel();
	private JPanel painel3 = new JPanel();
	private JPanel painel4 = new JPanel();
	private JPanel painel5 = new JPanel();
	private JPanel painel6 = new JPanel();
	private JPanel painel7 = new JPanel();
	private JPanel painel8 = new JPanel();
	private JPanel painel9 = new JPanel();
	private JPanel painel10 = new JPanel();
	private JPanel painel11 = new JPanel();
	
	private JFrame janela = new JFrame("-- Midi Player Aledu --");
	private Container painel = janela.getContentPane();
	
	private JLabel label1 = new JLabel();
	private JLabel label2 = new JLabel();
	private JLabel label3 = new JLabel();
	private JLabel label4 = new JLabel();
	private JLabel label5 = new JLabel();
	private JLabel label6 = new JLabel();
	private JLabel label7 = new JLabel();
	private JLabel label8 = new JLabel();
	private JLabel label9 = new JLabel();
	private JLabel label10 = new JLabel();
	private JLabel label11 = new JLabel();
	private JLabel label12 = new JLabel();
	private JLabel label13 = new JLabel();
	private JLabel label14 = new JLabel();
	private JLabel label15 = new JLabel();
	
	private int volume = 75;
	private JSlider sliderVolume = new JSlider(JSlider.HORIZONTAL,0,127,volume);
	private JProgressBar sliderTempo = new JProgressBar();
	
	private ModelMidiPlayer model = new ModelMidiPlayer();
	
	private long duracao     = model.getSequence().getMicrosecondLength()/1000000;
    private int  resolucao   = model.getSequence().getResolution();
    private long totaltiques = model.getSequence().getTickLength();
    private float durtique       = (float)duracao/totaltiques;
    private float durseminima    = durtique*resolucao;
    private float bpm            = 60/durseminima;
    private int   totalseminimas = (int)(duracao/durseminima);
	
	public ViewMidiPlayer() {
		//atribuição das ações dos botões
		botaoPlay.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				model.startPlayer();
			}
		});
		
		botaoPause.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				model.pausePlayer();
			}
		});
		
		botaoStop.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				model.stopPlayer();
			}
		});
		
		botaoAbrir.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				JFileChooser fc = new JFileChooser("../midi");
				FileNameExtensionFilter filefilter = new FileNameExtensionFilter("midi files","mid","midi");
				fc.setFileFilter(filefilter);
				int returnValue = fc.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fc.getSelectedFile();
					model.abrirArquivoMidi(selectedFile.getPath());
					
					duracao     = model.getSequence().getMicrosecondLength()/1000000;
					resolucao   = model.getSequence().getResolution();
					totaltiques = model.getSequence().getTickLength();
					
					durtique       = (float)duracao/totaltiques;
					durseminima    = durtique*resolucao;
					bpm            = 60/durseminima;
					totalseminimas = (int)(duracao/durseminima);
					
					label2.setText(model.getArqmidiName());
					label4.setText(model.getFormulaDeCompasso());
					label6.setText(model.getTonalidade());
					label8.setText(Integer.toString(Math.round(bpm)) + "bpm");
					label11.setText(String.valueOf(durtique) + " s");
					label13.setText(String.valueOf(totaltiques));
					label15.setText(String.valueOf(durseminima) + " s");
					janela.pack();
				}
				
			}
		});
		
		botaoBanco.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				JFileChooser fc = new JFileChooser("../soundfonts");
				FileNameExtensionFilter filefilter = new FileNameExtensionFilter("soundfonts","sf2");
				fc.setFileFilter(filefilter);
				int returnValue = fc.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fc.getSelectedFile();
					ModelMidiPlayer.carregarBANCO(selectedFile.getPath());
				}
			}
		});
		
		botaoInstrumentos.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				JFrame instrumentosDialog = new JFrame();
				Container painelInstrumentos = instrumentosDialog.getContentPane();
				JPanel painelInstrumentosDialog = new JPanel(new FlowLayout());
				DefaultTableModel dados = new DefaultTableModel();

				JTable tabela = new JTable(dados);
				dados.addColumn("nº");dados.addColumn("Instrumento");
				DefaultTableModel modelo = (DefaultTableModel) tabela.getModel();
				
				Instrument[] instrumentos = ModelMidiPlayer.getSintetizador().getLoadedInstruments();
				
				for(int i = 0; i<instrumentos.length; i++){
					modelo.addRow(new Object[]{i,instrumentos[i].getName()});
				}
				
				JScrollPane rolar = new JScrollPane();
				rolar.getViewport().setBorder(null);
				rolar.getViewport().add(tabela);
				rolar.setSize(450, 450);
				
				painelInstrumentosDialog.add(rolar);
				painelInstrumentos.setLayout(new GridLayout(1,0));
				painelInstrumentos.add(painelInstrumentosDialog);
				
				instrumentosDialog.pack();
				
				instrumentosDialog.setVisible(true);
			}
		});
		
		sliderVolume.addChangeListener(new ChangeListener() {
			public void stateChanged (ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				if(!source.getValueIsAdjusting()) {
					volume = model.mudaVolume((int)source.getValue());
					//System.out.println(volume);
				}
			}
		});
		
		//montagem da tela do software
		
		duracao     = model.getSequence().getMicrosecondLength()/1000000;
	    resolucao   = model.getSequence().getResolution();
	    totaltiques = model.getSequence().getTickLength();
	    durtique       = (float)duracao/totaltiques;
	    durseminima    = durtique*resolucao;
	    bpm            = 60/durseminima;
	    totalseminimas = (int)(duracao/durseminima);
		
		label1.setText("Arquivo: ");
		label2.setText(model.getArqmidiName());
		label3.setText("Armadura de Compasso: ");
		label4.setText(model.getFormulaDeCompasso());
		label5.setText("Tonalidade: ");
		label6.setText(model.getTonalidade());
		label7.setText("Andamento: ");
		label8.setText(Integer.toString(Math.round(bpm)) + " bpm");
		label9.setText("Volume: ");
		label10.setText("Duração do tique: ");
		label11.setText(String.valueOf(durtique) + " s");
		label12.setText("Total de tiques: ");
		label13.setText(String.valueOf(totaltiques));
		label14.setText("Duração Seminima: ");
		label15.setText(String.valueOf(durseminima) + " s");
		
		painel1.add(botaoAbrir);
		painel1.add(label1);
		painel1.add(label2);
		painel2.add(botaoBanco);
		painel2.add(botaoPlay);
		painel2.add(botaoPause);
		painel2.add(botaoStop);
		painel3.add(botaoBanco);
		painel3.add(botaoInstrumentos);
		painel4.add(label9);
		painel4.add(sliderVolume);
		painel5.add(label3);
		painel5.add(label4);
		painel6.add(label5);
		painel6.add(label6);
		painel7.add(label7);
		painel7.add(label8);
		painel8.add(label10);
		painel8.add(label11);
		painel9.add(label12);
		painel9.add(label13);
		painel10.add(label14);
		painel10.add(label15);
		painel11.add(sliderTempo);
		
		painel.setLayout(new GridLayout(11,0));
		painel.add(painel1);
		painel.add(painel2);
		painel.add(painel11);
		painel.add(painel4);
		painel.add(painel3);
		painel.add(painel5);
		painel.add(painel6);
		painel.add(painel7);
		painel.add(painel8);
		painel.add(painel9);
		painel.add(painel10);
		
		janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		janela.pack();
	}
	
	public void run() {
    	double dur;
    	double t;
    	int pos = 0;

    	while (true) {
    		dur = model.getDuracao() / 1000000;
    		t = model.getPosicao() / 1000000;
    		pos = (int) ((t * 100) / dur);
    		
    		if(t != dur) {
    			sliderTempo.setValue(pos);
    		}    			
    	}
	}

	
	public void executar(){
		janela.setVisible(true);
	}
	
}
