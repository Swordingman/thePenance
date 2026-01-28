/*     */ package com.esotericsoftware.spine38.attachments;
/*     */ 
/*     */ import com.badlogic.gdx.graphics.Color;
/*     */ import com.badlogic.gdx.graphics.g2d.TextureAtlas;
/*     */ import com.badlogic.gdx.graphics.g2d.TextureRegion;
/*     */ import com.esotericsoftware.spine38.Bone;
/*     */ import com.esotericsoftware.spine38.utils.SpineUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RegionAttachment
/*     */   extends Attachment
/*     */ {
/*     */   public static final int BLX = 0;
/*     */   public static final int BLY = 1;
/*     */   public static final int ULX = 2;
/*     */   public static final int ULY = 3;
/*     */   public static final int URX = 4;
/*     */   public static final int URY = 5;
/*     */   public static final int BRX = 6;
/*     */   public static final int BRY = 7;
/*     */   private TextureRegion region;
/*     */   private String path;
/*     */   private float x;
/*     */   private float y;
/*  56 */   private float scaleX = 1.0F; private float scaleY = 1.0F; private float rotation;
/*  57 */   private final float[] uvs = new float[8]; private float width; private float height;
/*  58 */   private final float[] offset = new float[8];
/*  59 */   private final Color color = new Color(1.0F, 1.0F, 1.0F, 1.0F);
/*     */   
/*     */   public RegionAttachment(String name) {
/*  62 */     super(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateOffset() {
/*  67 */     float width = getWidth();
/*  68 */     float height = getHeight();
/*  69 */     float localX2 = width / 2.0F;
/*  70 */     float localY2 = height / 2.0F;
/*  71 */     float localX = -localX2;
/*  72 */     float localY = -localY2;
/*  73 */     if (this.region instanceof TextureAtlas.AtlasRegion) {
/*  74 */       TextureAtlas.AtlasRegion region = (TextureAtlas.AtlasRegion)this.region;
/*  75 */       localX += region.offsetX / region.originalWidth * width;
/*  76 */       localY += region.offsetY / region.originalHeight * height;
/*  77 */       if (region.rotate) {
/*  78 */         localX2 -= (region.originalWidth - region.offsetX - region.packedHeight) / region.originalWidth * width;
/*  79 */         localY2 -= (region.originalHeight - region.offsetY - region.packedWidth) / region.originalHeight * height;
/*     */       } else {
/*  81 */         localX2 -= (region.originalWidth - region.offsetX - region.packedWidth) / region.originalWidth * width;
/*  82 */         localY2 -= (region.originalHeight - region.offsetY - region.packedHeight) / region.originalHeight * height;
/*     */       } 
/*     */     } 
/*  85 */     float scaleX = getScaleX();
/*  86 */     float scaleY = getScaleY();
/*  87 */     localX *= scaleX;
/*  88 */     localY *= scaleY;
/*  89 */     localX2 *= scaleX;
/*  90 */     localY2 *= scaleY;
/*  91 */     float rotation = getRotation();
/*  92 */     float cos = (float)Math.cos((0.017453292F * rotation));
/*  93 */     float sin = (float)Math.sin((0.017453292F * rotation));
/*  94 */     float x = getX();
/*  95 */     float y = getY();
/*  96 */     float localXCos = localX * cos + x;
/*  97 */     float localXSin = localX * sin;
/*  98 */     float localYCos = localY * cos + y;
/*  99 */     float localYSin = localY * sin;
/* 100 */     float localX2Cos = localX2 * cos + x;
/* 101 */     float localX2Sin = localX2 * sin;
/* 102 */     float localY2Cos = localY2 * cos + y;
/* 103 */     float localY2Sin = localY2 * sin;
/* 104 */     float[] offset = this.offset;
/* 105 */     offset[0] = localXCos - localYSin;
/* 106 */     offset[1] = localYCos + localXSin;
/* 107 */     offset[2] = localXCos - localY2Sin;
/* 108 */     offset[3] = localY2Cos + localXSin;
/* 109 */     offset[4] = localX2Cos - localY2Sin;
/* 110 */     offset[5] = localY2Cos + localX2Sin;
/* 111 */     offset[6] = localX2Cos - localYSin;
/* 112 */     offset[7] = localYCos + localX2Sin;
/*     */   }
/*     */   
/*     */   public void setRegion(TextureRegion region) {
/* 116 */     if (region == null) throw new IllegalArgumentException("region cannot be null."); 
/* 117 */     this.region = region;
/* 118 */     float[] uvs = this.uvs;
/* 119 */     if (region instanceof TextureAtlas.AtlasRegion && ((TextureAtlas.AtlasRegion)region).rotate) {
/* 120 */       uvs[4] = region.getU();
/* 121 */       uvs[5] = region.getV2();
/* 122 */       uvs[6] = region.getU();
/* 123 */       uvs[7] = region.getV();
/* 124 */       uvs[0] = region.getU2();
/* 125 */       uvs[1] = region.getV();
/* 126 */       uvs[2] = region.getU2();
/* 127 */       uvs[3] = region.getV2();
/*     */     } else {
/* 129 */       uvs[2] = region.getU();
/* 130 */       uvs[3] = region.getV2();
/* 131 */       uvs[4] = region.getU();
/* 132 */       uvs[5] = region.getV();
/* 133 */       uvs[6] = region.getU2();
/* 134 */       uvs[7] = region.getV();
/* 135 */       uvs[0] = region.getU2();
/* 136 */       uvs[1] = region.getV2();
/*     */     } 
/*     */   }
/*     */   
/*     */   public TextureRegion getRegion() {
/* 141 */     if (this.region == null) throw new IllegalStateException("Region has not been set: " + this); 
/* 142 */     return this.region;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void computeWorldVertices(Bone bone, float[] worldVertices, int offset, int stride) {
/* 153 */     float[] vertexOffset = this.offset;
/* 154 */     float x = bone.getWorldX(), y = bone.getWorldY();
/* 155 */     float a = bone.getA(), b = bone.getB(), c = bone.getC(), d = bone.getD();
/*     */ 
/*     */     
/* 158 */     float offsetX = vertexOffset[6];
/* 159 */     float offsetY = vertexOffset[7];
/* 160 */     worldVertices[offset] = offsetX * a + offsetY * b + x;
/* 161 */     worldVertices[offset + 1] = offsetX * c + offsetY * d + y;
/* 162 */     offset += stride;
/*     */     
/* 164 */     offsetX = vertexOffset[0];
/* 165 */     offsetY = vertexOffset[1];
/* 166 */     worldVertices[offset] = offsetX * a + offsetY * b + x;
/* 167 */     worldVertices[offset + 1] = offsetX * c + offsetY * d + y;
/* 168 */     offset += stride;
/*     */     
/* 170 */     offsetX = vertexOffset[2];
/* 171 */     offsetY = vertexOffset[3];
/* 172 */     worldVertices[offset] = offsetX * a + offsetY * b + x;
/* 173 */     worldVertices[offset + 1] = offsetX * c + offsetY * d + y;
/* 174 */     offset += stride;
/*     */     
/* 176 */     offsetX = vertexOffset[4];
/* 177 */     offsetY = vertexOffset[5];
/* 178 */     worldVertices[offset] = offsetX * a + offsetY * b + x;
/* 179 */     worldVertices[offset + 1] = offsetX * c + offsetY * d + y;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float[] getOffset() {
/* 186 */     return this.offset;
/*     */   }
/*     */   
/*     */   public float[] getUVs() {
/* 190 */     return this.uvs;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getX() {
/* 195 */     return this.x;
/*     */   }
/*     */   
/*     */   public void setX(float x) {
/* 199 */     this.x = x;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getY() {
/* 204 */     return this.y;
/*     */   }
/*     */   
/*     */   public void setY(float y) {
/* 208 */     this.y = y;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getScaleX() {
/* 213 */     return this.scaleX;
/*     */   }
/*     */   
/*     */   public void setScaleX(float scaleX) {
/* 217 */     this.scaleX = scaleX;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getScaleY() {
/* 222 */     return this.scaleY;
/*     */   }
/*     */   
/*     */   public void setScaleY(float scaleY) {
/* 226 */     this.scaleY = scaleY;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getRotation() {
/* 231 */     return this.rotation;
/*     */   }
/*     */   
/*     */   public void setRotation(float rotation) {
/* 235 */     this.rotation = rotation;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getWidth() {
/* 240 */     return this.width;
/*     */   }
/*     */   
/*     */   public void setWidth(float width) {
/* 244 */     this.width = width;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getHeight() {
/* 249 */     return this.height;
/*     */   }
/*     */   
/*     */   public void setHeight(float height) {
/* 253 */     this.height = height;
/*     */   }
/*     */ 
/*     */   
/*     */   public Color getColor() {
/* 258 */     return this.color;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPath() {
/* 263 */     return this.path;
/*     */   }
/*     */   
/*     */   public void setPath(String path) {
/* 267 */     this.path = path;
/*     */   }
/*     */   
/*     */   public Attachment copy() {
/* 271 */     RegionAttachment copy = new RegionAttachment(this.name);
/* 272 */     copy.region = this.region;
/* 273 */     copy.path = this.path;
/* 274 */     copy.x = this.x;
/* 275 */     copy.y = this.y;
/* 276 */     copy.scaleX = this.scaleX;
/* 277 */     copy.scaleY = this.scaleY;
/* 278 */     copy.rotation = this.rotation;
/* 279 */     copy.width = this.width;
/* 280 */     copy.height = this.height;
/* 281 */     SpineUtils.arraycopy(this.uvs, 0, copy.uvs, 0, 8);
/* 282 */     SpineUtils.arraycopy(this.offset, 0, copy.offset, 0, 8);
/* 283 */     copy.color.set(this.color);
/* 284 */     return copy;
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\attachments\RegionAttachment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */