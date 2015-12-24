import javax.sound.midi.*;
import java.io.*;
import java.lang.Math;

public class ModelMidiPlayer {
	private File arqmidi = new File("./midi/mvioloncelo1.mid");
	private static Soundbank    bancoSELECIONADO;
	private static Synthesizer  sintetizador = null;
	private static Sequencer sequenciador;
	private Sequence sequencia;
	private Receiver receptor = null;
	private final int FORMULA_DE_COMPASSO = 0x58;
	private static final int MENSAGEM_TONALIDADE = 0x59;
	private final int MESSAGEM_ANDAMENTO = 0x51;
	
	public ModelMidiPlayer() {
		try{
			sequencia = MidiSystem.getSequence(arqmidi);
			sequenciador = MidiSystem.getSequencer();
			sequenciador.setSequence(sequencia);
			sequenciador.open();
		}catch(MidiUnavailableException e1){
			System.out.println(e1);
		}catch(InvalidMidiDataException e2){
			System.out.println(e2);
		}catch(IOException e3){
			System.out.println(e3);
		}
	}
	
	public void abrirArquivoMidi(String arqmidiPath) {
		try{
			this.arqmidi = new File(arqmidiPath);
			sequencia = MidiSystem.getSequence(this.arqmidi);
			sequenciador.setSequence(sequencia);
		}catch(IOException e1){
			System.out.println(e1);
		}catch(InvalidMidiDataException e2){
			System.out.println(e2);
		}
	}
	
	public String getArqmidiName(){
		return arqmidi.getName();
	}
	
	public Sequence getSequence(){
		return sequencia;
	}
	
	public static Synthesizer getSintetizador() {
		return sintetizador;
	}
	
	public void startPlayer() {
		//para impedir que ele toque o midi várias vezes simultaneamente
		if(!sequenciador.isRunning()){
			sequenciador.start();
			receptor = sequenciador.getTransmitters().iterator().next().getReceiver();
			try {
				sequenciador.getTransmitter().setReceiver(receptor);
			} catch (MidiUnavailableException e1) {
				System.out.println(e1);
			}
		}
		//para voltar ao tempo inicial após o término do midi
		if(sequenciador.getMicrosecondPosition() == sequenciador.getMicrosecondLength()){
			sequenciador.setMicrosecondPosition(0);
			sequenciador.start();
			receptor = sequenciador.getTransmitters().iterator().next().getReceiver();
			try {
				sequenciador.getTransmitter().setReceiver(receptor);
			} catch (MidiUnavailableException e1) {
				System.out.println(e1);
			}
		}
	}
	
	public void stopPlayer() {
		//para o midi e volta ao tempo inicial
		sequenciador.stop();
		sequenciador.setMicrosecondPosition(0);
	}
	
	public void pausePlayer() {
		//para o midi mas mantem o tempo atual
		sequenciador.stop();
	}
	
	public int mudaVolume(int volume) {
		ShortMessage mensagemVolume = new ShortMessage();
		for(int i = 0; i<16; i++){
			
				try {
					mensagemVolume.setMessage(ShortMessage.CONTROL_CHANGE,i,7,volume);
					receptor.send(mensagemVolume, -1);
				} catch (InvalidMidiDataException e) {
					System.out.println(e);
				}
			
		}
		
		return volume;
	}
	
	public String getFormulaDeCompasso() {
		Track trilha = sequencia.getTracks()[0];
	    int p = 1;
	    int q = 1;
	    
	    for (int i = 0; i < trilha.size(); i++) {
	        MidiMessage m = trilha.get(i).getMessage();
	        if (m instanceof MetaMessage) {
	            if (((MetaMessage) m).getType() == FORMULA_DE_COMPASSO) {
	                MetaMessage mm = (MetaMessage) m;
	                byte[] data = mm.getData();
	                p = data[0];
	                q = data[1];
	            }
	        }
	     }
	     return p + "/" + (int)Math.pow(2, q);
	}
	
	public String getTonalidade()
	    {       
			Track trilha = sequencia.getTracks()[0];
	       String stonalidade = "";
	       for(int i=0; i<trilha.size(); i++)
	       { MidiMessage m = trilha.get(i).getMessage();
	       
	              
	       if(((MetaMessage)m).getType() == MENSAGEM_TONALIDADE)    
	       {
	            MetaMessage mm        = (MetaMessage)m;
	            byte[]     data       = mm.getData();
	            byte       tonalidade = data[0];
	            byte       maior      = data[1];
	
	            String       smaior = "Maior";
	            if(maior==1) smaior = "Menor";
	
	            if(smaior.equalsIgnoreCase("Maior"))
	            {
	                switch (tonalidade)
	                {
	                    case -7: stonalidade = "Dób Maior"; break;
	                    case -6: stonalidade = "Solb Maior"; break;
	                    case -5: stonalidade = "Réb Maior"; break;
	                    case -4: stonalidade = "Láb Maior"; break;
	                    case -3: stonalidade = "Mib Maior"; break;
	                    case -2: stonalidade = "Sib Maior"; break;
	                    case -1: stonalidade = "Fá Maior"; break;
	                    case  0: stonalidade = "Dó Maior"; break;
	                    case  1: stonalidade = "Sol Maior"; break;
	                    case  2: stonalidade = "Ré Maior"; break;
	                    case  3: stonalidade = "Lá Maior"; break;
	                    case  4: stonalidade = "Mi Maior"; break;
	                    case  5: stonalidade = "Si Maior"; break;
	                    case  6: stonalidade = "Fá# Maior"; break;
	                    case  7: stonalidade = "Dó# Maior"; break;
	                }
	            }
	
	            else if(smaior.equalsIgnoreCase("Menor"))
	            {
	                switch (tonalidade)
	                {
	                    case -7: stonalidade = "Láb Menor"; break;
	                    case -6: stonalidade = "Mib Menor"; break;
	                    case -5: stonalidade = "Sib Menor"; break;
	                    case -4: stonalidade = "Fá Menor"; break;
	                    case -3: stonalidade = "Dó Menor"; break;
	                    case -2: stonalidade = "Sol Menor"; break;
	                    case -1: stonalidade = "Ré Menor"; break;
	                    case  0: stonalidade = "Lá Menor"; break;
	                    case  1: stonalidade = "Mi Menor"; break;
	                    case  2: stonalidade = "Si Menor"; break;
	                    case  3: stonalidade = "Fá# Menor"; break;
	                    case  4: stonalidade = "Dó# Menor"; break;
	                    case  5: stonalidade = "Sol# Menor"; break;
	                    case  6: stonalidade = "Ré# Menor"; break;
	                    case  7: stonalidade = "Lá# Menor"; break;
	                }
	            }
	         }
	      }
	      return stonalidade;
	    }
	    
	    public float getAndamento(Track trilha)
		    {
				long microseg = 1;
				for(int i = 0; i < trilha.size(); i++){
					MidiMessage mensagem = trilha.get(i).getMessage();
					if(((MetaMessage) mensagem).getType() == MESSAGEM_ANDAMENTO)
					{
			            MetaMessage mm   = (MetaMessage)mensagem;
			            byte[]      data = mm.getData();
			
			            byte primeiro = data[0];
			            byte segundo  = data[1];
			            byte terceiro = data[2];
			
			            microseg = (long)(primeiro*Math.pow(2, 16) +
			                                   segundo *Math.pow(2,  8) +
			                                   terceiro
			                                  );
			
			            //int andamento = (int)(60000000.0/microseg);
			            //return "Andamento: " + andamento + " bpm";     
			                                           
			       }
		    	}
				return (float)(60000000.0/microseg);
		    }
	    
	    static void carregarBANCO(String bancoSF2_externo)
        {         
          try { sintetizador = MidiSystem.getSynthesizer();
                sintetizador.open();
              }
          catch (Exception ex) { System.out.println("Erro em MidiSystem.getSynthesizer(): " + ex);                                  
                                 return; 
                               }
                    
          Soundbank bancodefault = sintetizador.getDefaultSoundbank();
          if(bancodefault != null)
          { sintetizador.unloadAllInstruments(bancodefault);          
          }
          
          File arquivoSF2 = new File( bancoSF2_externo ); 
          if(!arquivoSF2.exists()) { System.out.println("Arquivo inexistente: " + bancoSF2_externo + "\n");
                                     System.exit(0);
                                   }

          try { bancoSELECIONADO = MidiSystem.getSoundbank(arquivoSF2); }
          catch (Exception e) { e.printStackTrace(); }

          sintetizador.loadAllInstruments( bancoSELECIONADO);

          try{ sequenciador.getTransmitter().setReceiver(sintetizador.getReceiver());
             }
          catch (Exception e) { System.out.println("Erro no carregamento do banco: "+e); }              

        }
	    
	    public double getDuracao() {
	    	return sequenciador.getMicrosecondLength();
	    }
	    
	    public double getPosicao() {
	    	return sequenciador.getMicrosecondPosition();
	    }
	    	
}
