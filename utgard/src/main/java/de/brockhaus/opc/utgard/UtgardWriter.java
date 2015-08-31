package de.brockhaus.opc.utgard;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.opc.lib.common.AlreadyConnectedException;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.AccessBase;
import org.openscada.opc.lib.da.AddFailedException;
import org.openscada.opc.lib.da.DataCallback;
import org.openscada.opc.lib.da.DuplicateGroupException;
import org.openscada.opc.lib.da.Group;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.SyncAccess;
 
public class UtgardWriter 
{
	static ConnectionInformation ci = new ConnectionInformation();
 	static Server server;
 	static String itemId;
 	
    public void init()
    {
    	// create connection information 
    	ci.setHost("asus");
    	ci.setDomain("");
    	ci.setUser("Julián Francisco");
    	ci.setPassword("09mayo");
    	ci.setClsid("F8582CF2-88FB-11D0-B850-00C0F0104305");
    	itemId = "Bucket Brigade.Int2";
    	// create a new server
    	server = new Server(ci, Executors.newSingleThreadScheduledExecutor());
    }

    public void connect() throws IllegalArgumentException, UnknownHostException, AlreadyConnectedException
    {
    	try 
    	{
    		// connect to server
    		server.connect();
    	}	 
    	catch (final JIException e) 
    	{
    		System.out.println(String.format("%08X: %s", e.getErrorCode(), server.getErrorMessage(e.getErrorCode())));
    	}
    }

    public void doWrite() throws InterruptedException, IllegalArgumentException, UnknownHostException, NotConnectedException, JIException, DuplicateGroupException, AddFailedException
    {
    	// add sync access, poll every 500 ms
    	final AccessBase access = new SyncAccess(server, 500);
    	access.addItem(itemId, new DataCallback()
    	{
    		public void changed(Item item, ItemState state)
    		{
            // also dump value
    			try 
    			{
    				if (state.getValue().getType() == JIVariant.VT_UI4) {
    					System.out.println("<<< " + state + " / value = " + state.getValue().getObjectAsUnsigned().getValue());
    				} 
    				else
    				{
    					System.out.println("<<< " + state + " / value = " + state.getValue().getObject());
    				}
    			} 
    			catch (JIException e)
    			{
    				e.printStackTrace();
    			}
    		}
    	});

    	// Add a new group
    	final Group group = server.addGroup("test");
    	// Add a new item to the group
    	final Item item = group.addItem(itemId);

    	// start reading
    	access.bind();

    	// add a thread for writing a value every 3 seconds
    	ScheduledExecutorService writeThread = Executors.newSingleThreadScheduledExecutor();
    	final AtomicInteger i = new AtomicInteger(0);
    	writeThread.scheduleWithFixedDelay(new Runnable()
    	{
    		public void run() 
    		{
    			final JIVariant value = new JIVariant(i.incrementAndGet());
    			try
    			{
    				System.out.println(">>> " + "writing value " + i.get());
    				item.write(value);
    			} 
    			catch (JIException e)
    			{
    				e.printStackTrace();
    			}
    		}
    	}, 5, 3, TimeUnit.SECONDS);

    	// wait a little bit
    	Thread.sleep(20 * 1000);
    	writeThread.shutdownNow();
    	// stop reading
    	access.unbind();
    }
}