package hack.pwn.gadaffi.database;

import hack.pwn.gadaffi.images.BitmapScaler;
import hack.pwn.gadaffi.steganography.Text;
import hack.pwn.gadaffi.test.R;
import android.graphics.Bitmap;

public class TextPeerTest extends DatabaseTestCase{
	
	private static final String IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer nec odio. Praesent libero. Sed cursus ante dapibus diam. Sed nisi. Nulla quis sem at nibh elementum imperdiet. Duis sagittis ipsum. Praesent mauris. Fusce nec tellus sed augue semper porta. Mauris massa. Vestibulum lacinia arcu eget nulla. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. \n\nCurabitur sodales ligula in libero. Sed dignissim lacinia nunc. Curabitur tortor. Pellentesque nibh. Aenean quam. In scelerisque sem at dolor. Maecenas mattis. Sed convallis tristique sem. Proin ut ligula vel nunc egestas porttitor. Morbi lectus risus, iaculis vel, suscipit quis, luctus non, massa. Fusce ac turpis quis ligula lacinia aliquet. Mauris ipsum. Nulla metus metus, ullamcorper vel, tincidunt sed, euismod in, nibh. \n\nQuisque volutpat condimentum velit. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nam nec ante. Sed lacinia, urna non tincidunt mattis, tortor neque adipiscing diam, a cursus ipsum ante quis turpis. Nulla facilisi. Ut fringilla. Suspendisse potenti. Nunc feugiat mi a tellus consequat imperdiet. Vestibulum sapien. Proin quam. Etiam ultrices. Suspendisse in justo eu magna luctus suscipit. \n\nSed lectus. Integer euismod lacus luctus magna. Quisque cursus, metus vitae pharetra auctor, sem massa mattis sem, at interdum magna augue eget diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Morbi lacinia molestie dui. Praesent blandit dolor. Sed non quam. In vel mi sit amet augue congue elementum. Morbi in ipsum sit amet pede facilisis laoreet. Donec lacus nunc, viverra nec, blandit vel, egestas et, augue. Vestibulum tincidunt malesuada tellus. Ut ultrices ultrices enim. Curabitur sit amet mauris. \n\nMorbi in dui quis est pulvinar ullamcorper. Nulla facilisi. Integer lacinia sollicitudin massa. Cras metus. Sed aliquet risus a tortor. Integer id quam. Morbi mi. Quisque nisl felis, venenatis tristique, dignissim in, ultrices sit amet, augue. Proin sodales libero eget ante. Nulla quam. Aenean laoreet. Vestibulum nisi lectus, commodo ac, facilisis ac, ultricies eu, pede. Ut orci risus, accumsan porttitor, cursus quis, aliquet eget, justo. \n";
	private Bitmap mCoverImage;

	@Override
	public void setUp() throws Exception {
		super.setUp();

		int target = 400;
		BitmapScaler scaler = new BitmapScaler(getContext().getResources(), R.drawable.flower, target);
		mCoverImage = scaler.getScaled();
		assertEquals(target, mCoverImage.getHeight());
		assertEquals(target, mCoverImage.getWidth());
		
	}
	
	public Text testInsertText() throws Exception {
		Text text = new Text();
		text.setText(IPSUM);
		text.setFrom("12345");
		
		TextPeer.insertText(text);
		
		return text;
	}
	
	public void testGetSingleText() throws Exception {
		Text original = testInsertText();
		Text saved = TextPeer.getTextById(original.getTextId());
		
		assertEquals(original.getTextId(), saved.getTextId());
		assertEquals(original.getPayloadId(), saved.getPayloadId());
		assertEquals(original.getFrom(), saved.getFrom());
	
	}

}
