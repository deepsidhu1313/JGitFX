package com.jgitfx.base.dialogs;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.DialogPane;
import javafx.util.Callback;

/**
 * A wrapper class for Dialog that removes some Dialog API options to insure
 * Git dialogs actually work via generics.
 *
 * <p>The following methods' access are dropped to protected:</p>
 * <ul>
 *     <li>{@link Dialog#setDialogPane(DialogPane)}</li>
 *     <li>{@link Dialog#setResultConverter(Callback)}</li>
 *     <li>{@link Dialog#setResult(Object)}</li>
 *     <li>{@link Dialog#dialogPaneProperty()}</li>
 *     <li>{@link Dialog#resultConverterProperty}</li>
 *     <li>{@link Dialog#resultProperty()}</li>
 * </ul>
 * @param <R> the return type of the dialog
 * @param <P> the type of {@link DialogPane} used in dialog
 */
public class GitDialog<R, P extends DialogPane> implements EventTarget {

    private Dialog<R> dialog = new Dialog<>();
    protected final Dialog<R> getDialog() { return dialog; }

    // prevents need to cast DialogPane into P
    private ObjectProperty<P> pane = new SimpleObjectProperty<>();
    public final void setDialogPane(P dialogPane) {
        pane.setValue(dialogPane);
        dialog.setDialogPane(dialogPane);
    }
    public final P getDialogPane() { return pane.getValue(); }
    public final ObjectProperty<P> dialogPaneProperty() { return pane; }

    // since this is a base/wrapper class, nothing to construct
    public GitDialog() {}

    // TODO: is this even needed?
    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        return dialog.buildEventDispatchChain(tail);
    }

    // protected methods

    protected final void setResult(R value) { dialog.setResult(value); }
    protected final ObjectProperty<R> resultProperty() { return dialog.resultProperty(); }

    protected final void setResultConverter(Callback<ButtonType, R> converter) { dialog.setResultConverter(converter); }
    protected final ObjectProperty<Callback<ButtonType, R>> resultConverterProperty() {return dialog.resultConverterProperty();}

    // public methods

    public final Callback<ButtonType, R> getResultConverter() {return dialog.getResultConverter();}

    public final R getResult() { return dialog.getResult(); }

    public final boolean isResizable() { return dialog.isResizable(); }
    public final void setResizable(boolean resizable) { dialog.setResizable(resizable); }

    public final void setTitle(String title) { dialog.setTitle(title);}
    public final String getTitle() {return dialog.getTitle();}

    public final void setContextText(String text) { dialog.setContentText(text);}
    public final String getContextText() {return dialog.getContentText(); }

    public final void setHeaderText(String text) {dialog.setHeaderText(text);}
    public final String getHeaderText() { return dialog.getHeaderText(); }

    public final void setGraphic(Node graphic) {dialog.setGraphic(graphic);}
    public final Node getGraphic() { return dialog.getGraphic(); }

    public final void setX(double x) {dialog.setX(x);}
    public final double getX() { return dialog.getX(); }

    public final void setY(double y) {dialog.setY(y);}
    public final double getY() {return dialog.getY();}

    public final void setHeight(double height) { dialog.setHeight(height);}
    public final double getHeight() { return dialog.getHeight(); }

    public final void setWidth(double width) {dialog.setWidth(width);}
    public final double getWidth() { return dialog.getWidth(); }

    public final void setOnShowing(EventHandler<DialogEvent> handler) { dialog.setOnShowing(handler);}
    public final EventHandler<DialogEvent> getOnShowing() { return dialog.getOnShowing(); }

    public final void setOnShown(EventHandler<DialogEvent> handler) { dialog.setOnShown(handler);}
    public final EventHandler<DialogEvent> getOnShown() { return dialog.getOnShown(); }

    public final void setOnCloseRequest(EventHandler<DialogEvent> handler) {dialog.setOnCloseRequest(handler);}
    public final EventHandler<DialogEvent> getOnCloseRequest() { return dialog.getOnCloseRequest(); }

    public final void setOnHiding(EventHandler<DialogEvent> handler) { dialog.setOnHiding(handler);}
    public final EventHandler<DialogEvent> getOnHiding() { return dialog.getOnHiding(); }

    public final void setOnHidden(EventHandler<DialogEvent> handler) { dialog.setOnHidden(handler);}
    public final EventHandler<DialogEvent> getOnHidden() { return dialog.getOnHidden(); }

}
