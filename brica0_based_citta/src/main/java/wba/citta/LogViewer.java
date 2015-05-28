package wba.citta;

import java.awt.Font;
import java.util.Collections;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import wba.citta.gui.ViewerPanel;
import wba.citta.util.EventPublisherSupport;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;

class DelegatingAppender extends AppenderBase<ILoggingEvent> implements LogMask {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;
 
    EventPublisherSupport<LoggingEvent, LoggingEventListener> logEntryAppendedListeners = new EventPublisherSupport<>(LoggingEvent.class, LoggingEventListener.class);
    int mask;
   
    public int getMask() {
        return mask;
    }

    public void setMask(int mask) {
        this.mask = mask;
    }

    public void addLoggingEventListener(LoggingEventListener listener) {
        logEntryAppendedListeners.addEventListener(listener);
    }

    public void removeLoggingEventListener(LoggingEventListener listener) {
        logEntryAppendedListeners.removeEventListener(listener);
    }

    @Override
    protected void append(ILoggingEvent evt) {
        switch (evt.getLevel().toInt()) {
        case Level.TRACE_INT:
            log(TRACE, evt.getMessage(), evt.getArgumentArray());
            break;
        case Level.DEBUG_INT:
            log(DEBUG, evt.getMessage(), evt.getArgumentArray());
            break;
        case Level.INFO_INT:
            log(INFO, evt.getMessage(), evt.getArgumentArray());
            break;
        case Level.WARN_INT:
            log(WARN, evt.getMessage(), evt.getArgumentArray());
            break;
        case Level.ERROR_INT:
            log(ERROR, evt.getMessage(), evt.getArgumentArray());
            break;
        }
    }

    public void log(int mask, String message, Object... args) {
        FormattingTuple t = MessageFormatter.arrayFormat(message, args);
        logEntryAppendedListeners.fire("logEntryAppended", new LoggingEvent(this, mask, t.getMessage()));
    }
}

public class LogViewer extends JScrollPane implements ViewerPanel, LoggingEventListener {
    private static final long serialVersionUID = 1L;
    private static final Set<String> roles = Collections.singleton("info");
    private static final DelegatingAppender appender = new DelegatingAppender();

    JTextArea textArea = new JTextArea();
    int maxLines = 100;

    public static Appender<ILoggingEvent> getAppender() {
        return appender;
    }

    public LogViewer() {
        super(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, textArea.getFont().getSize()));
        setViewportView(textArea);
        appender.addLoggingEventListener(this);
    }
    
    public int getMaxLines() {
        return maxLines;
    }

    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
    }

    static String logMaskToString(int mask) {
        switch (mask) {
        case LogMask.DEBUG:
            return "DEBUG";
        case LogMask.TRACE:
            return "TRACE";
        case LogMask.INFO:
            return "INFO";
        case LogMask.WARN:
            return "WARN";
        case LogMask.ERROR:
            return "ERROR";
        }
        return "?";
    }
    
    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public String getPreferredTitle() {
        return "Log Viewer";
    }

    @Override
    public void logEntryAppended(final LoggingEvent evt) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (textArea.getLineCount() >= maxLines) {
                    try {
                        textArea.replaceRange("", 0, textArea.getLineEndOffset(0));
                    } catch (BadLocationException e) {
                        // ignorable
                    }
                }
                textArea.append(String.format("[%s] %s\n", logMaskToString(evt.mask), evt.getMessage()));
            }
        });
    }

    @Override
    public Set<String> getViewerPanelRoles() {
        return roles;
    }
}