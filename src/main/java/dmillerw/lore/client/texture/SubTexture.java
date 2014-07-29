package dmillerw.lore.client.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @author dmillerw
 */
public class SubTexture {

	public static void drawTexturedModalRect(int x, int y, int z, int u, int v, int w, int h) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV((double) (x + 0), (double) (y + h), (double) z, (double) ((float) (u + 0) * f), (double) ((float) (v + h) * f1));
		tessellator.addVertexWithUV((double) (x + w), (double) (y + h), (double) z, (double) ((float) (u + w) * f), (double) ((float) (v + h) * f1));
		tessellator.addVertexWithUV((double) (x + w), (double) (y + 0), (double) z, (double) ((float) (u + w) * f), (double) ((float) (v + 0) * f1));
		tessellator.addVertexWithUV((double) (x + 0), (double) (y + 0), (double) z, (double) ((float) (u + 0) * f), (double) ((float) (v + 0) * f1));
		tessellator.draw();
	}

	private final ResourceLocation texture;

	private final int u;
	private final int v;
	private final int w;
	private final int h;

	public SubTexture(ResourceLocation texture, int u, int v, int w, int h) {
		this.texture = texture;
		this.u = u;
		this.v = v;
		this.w = w;
		this.h = h;
	}

	public void draw(int x, int y, int z) {
		// We don't want to bind textures unless necessary
		TextureManager manager = Minecraft.getMinecraft().getTextureManager();
		ITextureObject textureObject = manager.getTexture(texture);
		int bound = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
		if (textureObject == null || bound != textureObject.getGlTextureId()) {
			manager.bindTexture(texture);
		}

		// ...and then we draw
		SubTexture.drawTexturedModalRect(x, y, z, this.u, this.v, this.w, this.h);
	}

}
