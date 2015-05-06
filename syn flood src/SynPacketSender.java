import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.JMemoryPacket;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.protocol.JProtocol;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;


public class SynPacketSender {
	private JPacket packet;
	private Pcap pcap;
	private int port;

	public SynPacketSender() {
		createPacket();
		findDevice();
	}

	private void findDevice() {
		List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs  
	    StringBuilder errbuf = new StringBuilder(); // For any error msgs  
	  
	    /*************************************************************************** 
	     * First get a list of devices on this system 
	     **************************************************************************/  
	    int r = Pcap.findAllDevs(alldevs, errbuf);  
	    if (r == Pcap.NOT_OK || alldevs.isEmpty()) {  
	      System.err.printf("Can't read list of devices, error is %s", errbuf.toString());  
	      return;  
	    }
	    PcapIf device = alldevs.get(2); // We know we have atleast 1 device  
	  
	    /***************************************** 
	     * Second we open a network interface 
	     *****************************************/  
	    int snaplen = 64 * 1024; // Capture all packets, no trucation  
	    int flags = Pcap.MODE_PROMISCUOUS; // capture all packets  
	    int timeout = 10 * 1000; // 10 seconds in millis  
	    pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf); 
	}

	private void createPacket() {
		packet = new JMemoryPacket(JProtocol.ETHERNET_ID,  
		    " 001801bf 6adc0025 4bb7afec 08004500 " +  
		    " 0041a983 40004006 d69ac0a8 00342f8c " +  
		    " ca30c3ef 008f2e80 11f52ea8 4b57800A " +  
		    " ffffa6ea 00000101 080a152e ef03002a " +  
		    " 2c943538 322e3430 204e4f4f 500d0a");
			  
		              /* Our working headers we want to use to modify packet headers */  
		Ip4 ip = packet.getHeader(new Ip4());  
		Tcp tcp = packet.getHeader(new Tcp());
		Ethernet eth = packet.getHeader(new Ethernet());
		
		tcp.source(9001);
		tcp.destination(80); // Change dst port to 80
		byte[] dest = {10, 0, 0, 1};
		byte[] src = {10, 0, 0, 2};
		ip.destination(dest);
		ip.source(src);
		byte[] ethdest = {0x00, 0x26, (byte) 0x9E, (byte) 0xD5, (byte) 0xD2, (byte) 0xD4};
		byte[] ethsrc = {0x70, 0x18, (byte) 0x8B, 0x2B, (byte) 0xEF, 0x47};
		eth.destination(ethdest);
		eth.source(ethsrc);
		
		
		  
		              /* Recalculate header checksums */  
		ip.checksum(ip.calculateChecksum());  
		tcp.checksum(tcp.calculateChecksum());  
		  
		              /* Rescan packet for any structural changes */  
		packet.scan(Ethernet.ID);  
		  
		System.out.println(packet);
	}

	public void sendPacket() {
	    if (pcap.sendPacket(ByteBuffer.wrap(packet.getByteArray(0, packet.size()))) != Pcap.OK) {
	    	System.err.println(pcap.getErr());
	    }
	}
	

	public void close() {
		pcap.close();
	}

	public void changeSrcPort() {
		Tcp tcp = packet.getHeader(new Tcp());
		tcp.source((port)%64512+1024);
		tcp.checksum(tcp.calculateChecksum());
		port++;
	}
}
