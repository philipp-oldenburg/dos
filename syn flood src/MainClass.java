public class MainClass {

	public static void main(String[] args) {
		final SynPacketSender sender = new SynPacketSender();
		//long time = System.currentTimeMillis();
		//for (int j = 0; j < 1000; j++) {
		while (true) {
			sender.sendPacket();
			sender.changeSrcPort();
		}
		//System.out.println("speed:" + 1000.0/(System.currentTimeMillis() - time) + " Anfragen pro ms");
	}

}
