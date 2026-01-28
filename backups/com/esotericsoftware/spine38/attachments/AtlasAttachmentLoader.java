/*    */ package com.esotericsoftware.spine38.attachments;
/*    */ 
/*    */ import com.badlogic.gdx.graphics.g2d.TextureAtlas;
/*    */ import com.badlogic.gdx.graphics.g2d.TextureRegion;
/*    */ import com.esotericsoftware.spine38.Skin;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AtlasAttachmentLoader
/*    */   implements AttachmentLoader
/*    */ {
/*    */   private TextureAtlas atlas;
/*    */   
/*    */   public AtlasAttachmentLoader(TextureAtlas atlas) {
/* 46 */     if (atlas == null) throw new IllegalArgumentException("atlas cannot be null."); 
/* 47 */     this.atlas = atlas;
/*    */   }
/*    */   
/*    */   public RegionAttachment newRegionAttachment(Skin skin, String name, String path) {
/* 51 */     TextureAtlas.AtlasRegion region = this.atlas.findRegion(path);
/* 52 */     if (region == null) throw new RuntimeException("Region not found in atlas: " + path + " (region attachment: " + name + ")"); 
/* 53 */     RegionAttachment attachment = new RegionAttachment(name);
/* 54 */     attachment.setRegion((TextureRegion)region);
/* 55 */     return attachment;
/*    */   }
/*    */   
/*    */   public MeshAttachment newMeshAttachment(Skin skin, String name, String path) {
/* 59 */     TextureAtlas.AtlasRegion region = this.atlas.findRegion(path);
/* 60 */     if (region == null) throw new RuntimeException("Region not found in atlas: " + path + " (mesh attachment: " + name + ")"); 
/* 61 */     MeshAttachment attachment = new MeshAttachment(name);
/* 62 */     attachment.setRegion((TextureRegion)region);
/* 63 */     return attachment;
/*    */   }
/*    */   
/*    */   public BoundingBoxAttachment newBoundingBoxAttachment(Skin skin, String name) {
/* 67 */     return new BoundingBoxAttachment(name);
/*    */   }
/*    */   
/*    */   public ClippingAttachment newClippingAttachment(Skin skin, String name) {
/* 71 */     return new ClippingAttachment(name);
/*    */   }
/*    */   
/*    */   public PathAttachment newPathAttachment(Skin skin, String name) {
/* 75 */     return new PathAttachment(name);
/*    */   }
/*    */   
/*    */   public PointAttachment newPointAttachment(Skin skin, String name) {
/* 79 */     return new PointAttachment(name);
/*    */   }
/*    */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\attachments\AtlasAttachmentLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */