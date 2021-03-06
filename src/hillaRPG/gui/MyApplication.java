package hillaRPG.gui;

import static com.codename1.ui.CN.addNetworkErrorListener;
import static com.codename1.ui.CN.updateNetworkThreadCount;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.codename1.io.FileSystemStorage;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Font;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;

import hillaRPG.gui.telacriarpersonagem.CriarPersonagemForm;
import hillaRPG.gui.telaprincipal.PrincipalForm;
import hillaRPG.gui.util.AddBackCmd;
import hillaRPG.gui.util.Jogo;
import hillaRPG.gui.util.MeuBotao;
import hillaRPG.gui.util.MeuForm;
import hillaRPG.gui.util.MeuLabel;
import hillaRPG.personagem.Personagem;
import hillaRPG.personagem.Personagens;

/**
 * This file was generated by <a href="https://www.codenameone.com/">Codename One</a> for the purpose 
 * of building native mobile applications using Java.
 */
public class MyApplication {

    private Form current;
    private Resources theme;

    public void init(Object context) {
        // use two network threads instead of one
        updateNetworkThreadCount(2);

        theme = UIManager.initFirstTheme("/theme");

        // Enable Toolbar on all Forms by default
        Toolbar.setGlobalToolbar(true);

        // Pro only feature
        Log.bindCrashProtection(true);

        addNetworkErrorListener(err -> {
            // prevent the event from propagating
            err.consume();
            if(err.getError() != null) {
                Log.e(err.getError());
            }
            Log.sendLogAsync();
            Dialog.show("Connection Error", "There was a networking error in the connection to " + err.getConnectionRequest().getUrl(), "OK", null);
        });        
    }
    
    public void start() {
        if(current != null){
            current.show();
            return;
        }
        Form hi = new MeuForm(new BoxLayout(BoxLayout.Y_AXIS));
        
        paintBotoes(hi);
        
        Display.getInstance().setLongPointerPressInterval(1000);
        
        hi.show();
    }
    
    public void paintBotoes(Form hi) {
        String path = FileSystemStorage.getInstance().getAppHomePath() + "/personagens.json";
        InputStream is;
        try {
            is = FileSystemStorage.getInstance().openInputStream(path);
        } catch (Exception ex) {
			try {
				String str = "[ ]";
				OutputStream os = FileSystemStorage.getInstance().openOutputStream(path);
				os.write(str.getBytes("UTF-8"));
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        Label lbLoading = new MeuLabel();
        hi.add(lbLoading);
        Jogo.setup(lbLoading);
        hi.removeComponent(lbLoading);
        Personagens personagens = Jogo.getPersonagens();
        
		if(personagens.size() > 0) {
			for(int i = 0; i < personagens.size(); i++) {
				Personagem personagem = personagens.get(i);
				Button btn = new MeuBotao(personagem.getNome()) {
				    @Override
				    public void longPointerPress(int x, int y) {
				        super.longPointerPress(x, y);
				        
				        if(Dialog.show("Certeza?", "Deseja realmente remover o personagem?",
				        				"Sim", "Não")) {
				        	personagens.remove(personagem.getNome());
				        	personagens.atualizarInfoPersonagem();
				        	
				        	hi.removeAll();
				        	paintBotoes(hi);
				        	
				        	hi.repaint();
				        	hi.revalidate();
				        } else {
				        	
				        }
				    }
				};
				btn.getAllStyles().setMarginTop(2);
				btn.addActionListener((ae) -> {
					Jogo.setupPersonagem(personagem.getNome());
					
					Form form = new PrincipalForm();
					
					AddBackCmd.addBackCmd(Display.getInstance().getCurrent(), form);
					
					form.show();
				});
				hi.add(btn);
			}
		}
		
		Button btnCriarPersonagem = new MeuBotao();
		Font fnt = Font.createTrueTypeFont("fontello", "fontello.ttf");
		int size = Display.getInstance().convertToPixels(4);
		FontImage fm = FontImage.createFixed("\ue800", fnt, 0x000000, size, size);
		btnCriarPersonagem.setIcon(fm);
		btnCriarPersonagem.getAllStyles().setMarginTop(2);
		btnCriarPersonagem.addActionListener((ae) -> {
			Form form = new CriarPersonagemForm(new BoxLayout(BoxLayout.Y_AXIS));
			
			Command cmd = new Command("Voltar") {
				@Override
				public void actionPerformed(ActionEvent ae) {
					hi.showBack();
					
					hi.removeAll();
					paintBotoes(hi);
				}
			};
			form.getToolbar().setBackCommand(cmd);
			
			form.show();
		});
		hi.add(btnCriarPersonagem);
    }

    public void stop() {
        current = Display.getInstance().getCurrent();
        if(current instanceof Dialog) {
            ((Dialog)current).dispose();
            current = Display.getInstance().getCurrent();
        }
    }
    
    public void destroy() {
    }
}
