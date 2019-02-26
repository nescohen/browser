package org.reactome.web.pwp.client.details.tabs.analysis.widgets.filtering.pvalue;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.reactome.web.pwp.client.details.tabs.analysis.widgets.filtering.size.*;


/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SimpleSlider extends Composite implements HasHandlers,
        MouseMoveHandler, MouseDownHandler, MouseOutHandler, MouseUpHandler {
    private static NumberFormat formatter = NumberFormat.getFormat("#.00");
    private Point base;

    private Canvas canvas;
    private int width;
    private int height;
    private double min;
    private double max;
    private double filterMax;

    private Axis axis;
    private Thumb maxThumb;

    public SimpleSlider(int width, int height, double min, double max, double filterMax) {
        this.width = width;
        this.height = height;
        base = new Point(30, height - 20);

        if (min >= max) throw new RuntimeException("Min value in SimpleSlider has to be always lower than max.");
        this.min = min;
        this.max = max;

        this.filterMax = filterMax > max && filterMax < min ? max : filterMax;

        initUI();
        initHandlers();
        draw();
    }

    public HandlerRegistration addRangeValueChangedHandler(RangeValueChangedHandler handler){
        return addHandler(handler, RangeValueChangedEvent.TYPE);
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        Point point = getMousePosition(event);
        maxThumb.setStatus(maxThumb.contains(point) ? ThumbStatus.CLICKED : ThumbStatus.NORMAL);
        draw();
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        Point point = getMousePosition(event);
        int pos = point.x() - base.x();
        if (maxThumb.getStatus() == ThumbStatus.CLICKED) {
            double newPosition = pos > (width - 2 * base.x()) ? (width - 2 * base.x()) : pos;
            newPosition = newPosition < 0 ? 0 : newPosition;
            filterMax = translatePointOnAxisToValue(newPosition);
            maxThumb.setPosition(newPosition, formatter.format(filterMax));
            axis.setFilterMax(newPosition);
            fireEvent(new RangeValueChangedEvent(0, filterMax));
        } else {
            boolean isHovered = maxThumb.contains(point);
            maxThumb.setStatus(isHovered ? ThumbStatus.HOVERED : ThumbStatus.NORMAL);
            getElement().getStyle().setCursor(isHovered ? Style.Cursor.POINTER : Style.Cursor.DEFAULT);
        }
        draw();
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        Point point = getMousePosition(event);
        maxThumb.setStatus(maxThumb.contains(point) ? ThumbStatus.HOVERED : ThumbStatus.NORMAL);
        draw();
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        //TODO implement this
    }

    public void setValue(double value) {
        this.filterMax = value;
        double tFilterMax = translateValueToPointOnAxis(filterMax);
        maxThumb.setPosition(tFilterMax, formatter.format(filterMax));
        axis.setFilterMax(tFilterMax);
        draw();
    }

    private void initUI() {
        canvas = Canvas.createIfSupported();
        if (canvas !=null) {
            canvas.setWidth(width + "px");
            canvas.setHeight(height + "px");
            canvas.setCoordinateSpaceWidth(width);
            canvas.setCoordinateSpaceHeight(height);
            canvas.setStyleName(RESOURCES.getCSS().simpleSlider());
            FlowPanel main = new FlowPanel();
            main.add(canvas);

            initWidget(main);
            initialize();
        }
    }

    private void initHandlers() {
        canvas.addMouseDownHandler(this);
        canvas.addMouseMoveHandler(this);
        canvas.addMouseOutHandler(this);
        canvas.addMouseUpHandler(this);
    }

    private void initialize() {
        double tFilterMax = translateValueToPointOnAxis(filterMax);
        axis = new Axis(base, min, max, tFilterMax);
        maxThumb = new Thumb(base, tFilterMax, formatter.format(filterMax));
        maxThumb.setFont("bold 11px Arial");

    }

    private double translateValueToPointOnAxis(double value) {
        double p = ((width - 2 * base.x()) * (value - min) / (double)(max - min));
        return Math.round(p);
    }

    private double translatePointOnAxisToValue(double point) {
        double value = ((max - min) * point / (double)(width - 2 * base.x())) + min;
        return value;
    }

    private void draw(){
        Context2d ctx = canvas.getContext2d();
        ctx.clearRect(0, 0, width, height);
        axis.draw(ctx);
        maxThumb.draw(ctx);
    }

    private Point getMousePosition(MouseEvent event){
        int x = event.getRelativeX(this.canvas.getElement());
        int y = event.getRelativeY(this.canvas.getElement());
        return new Point(x,y);
    }


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();
    }

    @CssResource.ImportedWithPrefix("pwp-SimpleSlider")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/pwp/client/details/tabs/analysis/widgets/filtering/pvalue/SimpleSlider.css";

        String simpleSlider();

    }
}