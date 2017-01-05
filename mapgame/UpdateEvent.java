import java.util.EventListener;
import java.util.EventObject;

public interface UpdateEvent
{
    public static class Event extends EventObject
    {
        private Object value;
        public Event(UpdateEvent model, Object value)
        {
            super(value);
            this.value = value;
        }
        public Object getValue()
        {
            return(value);
        }
    }
    public interface Listener extends EventListener
    {
        public void valueChanged(UpdateEvent.Event e);
    }
    public void setValue(Object obj);
    public Object getValue();

    public void addListener(UpdateEvent.Listener l);
    public void removeListener(UpdateEvent.Listener l);
}
