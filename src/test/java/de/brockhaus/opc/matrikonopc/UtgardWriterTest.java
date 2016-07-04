package de.brockhaus.opc.matrikonopc;

import java.net.UnknownHostException;

import org.jinterop.dcom.common.JIException;
import org.junit.Test;
import org.openscada.opc.lib.common.AlreadyConnectedException;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.AddFailedException;
import org.openscada.opc.lib.da.DuplicateGroupException;

import de.brockhaus.opc.matrikonopc.UtgardWriter;

public class UtgardWriterTest
{
	@Test
	public void test() throws IllegalArgumentException, UnknownHostException, AlreadyConnectedException, NotConnectedException, JIException, DuplicateGroupException, AddFailedException, InterruptedException
	{
		UtgardWriter uw = new UtgardWriter();
		uw.init();
		uw.connect();
		uw.doWrite(); 
	}
}