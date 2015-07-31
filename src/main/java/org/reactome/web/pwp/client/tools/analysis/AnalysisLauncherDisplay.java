package org.reactome.web.pwp.client.tools.analysis;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.pwp.client.tools.analysis.submitters.FileSubmitter;
import org.reactome.web.pwp.client.tools.analysis.submitters.PostSubmitter;
import org.reactome.web.pwp.client.tools.launcher.LauncherButton;
import org.reactome.web.pwp.client.tools.analysis.event.AnalysisCompletedEvent;
import org.reactome.web.pwp.client.tools.analysis.event.AnalysisErrorEvent;
import org.reactome.web.pwp.client.tools.analysis.handler.AnalysisCompletedHandler;
import org.reactome.web.pwp.client.tools.analysis.handler.AnalysisErrorEventHandler;
import org.reactome.web.pwp.client.tools.analysis.submitters.SpeciesSubmitter;
import org.reactome.web.pwp.model.classes.Species;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class AnalysisLauncherDisplay extends PopupPanel implements AnalysisLauncher.Display, ResizeHandler, AnalysisCompletedHandler, AnalysisErrorEventHandler, ClickHandler, CloseHandler<PopupPanel> {

    private AnalysisLauncher.Presenter presenter;

    private List<Button> btns = new LinkedList<>();
    private Button analysisBtn;
    private Button speciesBtn;

    private DeckLayoutPanel container;

    private SpeciesSubmitter speciesSubmitter;

    public AnalysisLauncherDisplay() {
        super();
        this.setAutoHideEnabled(true);
        this.setModal(true);
        this.setAnimationEnabled(true);
        this.setGlassEnabled(true);
        this.setAutoHideOnHistoryEventsEnabled(false);
        this.addStyleName(RESOURCES.getCSS().popupPanel());
        Window.addResizeHandler(this);

        int width = (int) Math.round(Window.getClientWidth() * 0.9);
        int height = (int) Math.round(Window.getClientHeight() * 0.9);
        this.setWidth(width + "px");
        this.setHeight(height + "px");

        FlowPanel vp = new FlowPanel();                         // Main panel
        vp.addStyleName(RESOURCES.getCSS().analysisPanel());
        vp.add(setTitlePanel());                                // Title panel with label & button

        FlowPanel buttonsPanel = new FlowPanel();               // Tab buttons panel
        buttonsPanel.setStyleName(RESOURCES.getCSS().buttonsPanel());
        buttonsPanel.addStyleName(RESOURCES.getCSS().unselectable());
        buttonsPanel.add(this.analysisBtn = getButton("Analysis Tool", RESOURCES.analysisTabIcon()));
        buttonsPanel.add(this.speciesBtn = getButton("Species Comparison", RESOURCES.speciesTabIcon()));
        this.analysisBtn.addStyleName(RESOURCES.getCSS().buttonSelected());

        this.container = new DeckLayoutPanel();                 // Main tab container
        this.container.setStyleName(RESOURCES.getCSS().container());

        PostSubmitter postSubmitter = new PostSubmitter();
        postSubmitter.addAnalysisCompletedEventHandler(this);
        postSubmitter.addAnalysisErrorEventHandler(this);

        FileSubmitter fileSubmitter = new FileSubmitter(postSubmitter);
        fileSubmitter.addAnalysisCompletedEventHandler(this);
        fileSubmitter.addAnalysisErrorEventHandler(this);

        this.speciesSubmitter = new SpeciesSubmitter();
        this.speciesSubmitter.addAnalysisCompletedEventHandler(this);
        this.speciesSubmitter.addAnalysisErrorEventHandler(this);

        this.container.add(fileSubmitter);
        this.container.add(speciesSubmitter);

        this.container.showWidget(0);
        this.container.setAnimationVertical(true);
        this.container.setAnimationDuration(500);

        FlowPanel outerPanel = new FlowPanel();                 // Vertical tab Panel and buttons container
        outerPanel.setStyleName(RESOURCES.getCSS().outerPanel());
        outerPanel.add(buttonsPanel);
        outerPanel.add(this.container);

        vp.add(outerPanel);

        this.addCloseHandler(this);

        this.add(vp);
    }

    public Button getButton(String text, ImageResource imageResource){
        FlowPanel fp = new FlowPanel();
        Image image = new Image(imageResource);
        image.addStyleName(RESOURCES.getCSS().undraggable());
        fp.add(image);
        fp.add(new Label(text));

        SafeHtml safeHtml = SafeHtmlUtils.fromSafeConstant(fp.toString());
        Button btn = new Button(safeHtml, this);
        this.btns.add(btn);
        return btn;
    }

    private Widget setTitlePanel(){
        FlowPanel header = new FlowPanel();
        header.setStyleName(RESOURCES.getCSS().header());
        Label title = new Label("Analyse your data");
        title.setStyleName(RESOURCES.getCSS().headerText());
        Button closeBtn = new LauncherButton("close", RESOURCES.getCSS().close(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                AnalysisLauncherDisplay.this.hide();
            }
        });
        header.add(title);
        header.add(closeBtn);
        return header;
    }

    @Override
    public void onAnalysisCompleted(AnalysisCompletedEvent event) {
        presenter.analysisCompleted(event);
    }

    @Override
    public void onAnalysisError(AnalysisErrorEvent event) {
        presenter.analysisError(event);
    }

    @Override
    public void onClick(ClickEvent event) {
        for (Button btn : btns) {
            btn.removeStyleName(RESOURCES.getCSS().buttonSelected());
        }
        Button btn = (Button) event.getSource();
        btn.addStyleName(RESOURCES.getCSS().buttonSelected());
        if(btn.equals(this.analysisBtn)){
            this.container.showWidget(0);
        }else if(btn.equals(this.speciesBtn)){
            this.container.showWidget(1);
        }
    }

    @Override
    public void onClose(CloseEvent<PopupPanel> event) {
        presenter.displayClosed();
    }

    @Override
    public void onResize(ResizeEvent event) {
        if(isVisible()){
            int width = (int) Math.round(RootLayoutPanel.get().getOffsetWidth() * 0.9);
            int height = (int) Math.round(RootLayoutPanel.get().getOffsetHeight() * 0.9);
            this.setWidth(width + "px");
            this.setHeight(height + "px");
        }
    }

    @Override
    public void setPresenter(AnalysisLauncher.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setSpeciesList(List<Species> speciesList) {
        this.speciesSubmitter.setSpeciesList(speciesList);
    }


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface Resources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("images/analysisTabIcon.png")
        ImageResource analysisTabIcon();

        @Source("images/close_clicked.png")
        ImageResource closeClicked();

        @Source("images/close_hovered.png")
        ImageResource closeHovered();

        @Source("images/close_normal.png")
        ImageResource closeNormal();

        @Source("images/speciesTabIcon.png")
        ImageResource speciesTabIcon();

    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("pwp-AnalysisLauncher")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/pwp/client/tools/analysis/AnalysisLauncher.css";

        String popupPanel();

        String analysisPanel();

        String header();

        String headerText();

        String close();

        String outerPanel();

        String buttonsPanel();

        String unselectable();

        String undraggable();

        String buttonSelected();

        String container();
    }
}
