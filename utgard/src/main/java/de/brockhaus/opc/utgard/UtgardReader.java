package de.brockhaus.opc.utgard;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.common.AlreadyConnectedException;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.AccessBase;
import org.openscada.opc.lib.da.AddFailedException;
import org.openscada.opc.lib.da.DataCallback;
import org.openscada.opc.lib.da.DuplicateGroupException;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.SyncAccess;
 
 public class UtgardReader 
 {
	static ConnectionInformation ci = new ConnectionInformation();
 	static Server server;
 	static String itemId;
    
    public void init()
    {
    	// create connection information 
        ci.setHost("srumpf-work");
        ci.setDomain("");
        ci.setUser("Administrator");
        ci.setPassword("Admin");
        ci.setClsid("F8582CF2-88FB-11D0-B850-00C0F0104305");
        itemId = "Saw-toothed Waves.Int2";
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
    
    public void doRead() throws IllegalArgumentException, UnknownHostException, NotConnectedException, JIException, DuplicateGroupException, AddFailedException, InterruptedException
    {
    	// add sync access, poll every 1000 ms
        final AccessBase access = new SyncAccess(server,1000);
        access.addItem(itemId, new DataCallback() {
            public void changed(Item item, ItemState state) 
            {
                System.out.println(state);
            }
        });
        // start reading
        access.bind();
        // wait a little bit
        Thread.sleep(10 * 1000);
        // stop reading
        access.unbind();
    }
 }
