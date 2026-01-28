/*     */ package com.esotericsoftware.spine38.attachments;
/*     */ 
/*     */ import com.badlogic.gdx.graphics.Color;
/*     */ import com.badlogic.gdx.graphics.g2d.TextureAtlas;
/*     */ import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
/*     */ 
/*     */ public class MeshAttachment
/*     */   extends VertexAttachment
/*     */ {
/*     */   private TextureRegion region;
/*     */   private String path;
/*     */   private float[] regionUVs;
/*     */   private float[] uvs;
/*     */   private short[] triangles;
/*  49 */   private final Color color = new Color(1.0F, 1.0F, 1.0F, 1.0F);
/*     */   
/*     */   private int hullLength;
/*     */   private MeshAttachment parentMesh;
/*     */   private short[] edges;
/*     */   private float width;
/*     */   private float height;
/*     */   
/*     */   public MeshAttachment(String name) {
/*  58 */     super(name);
/*     */   }
/*     */   
/*     */   public void setRegion(TextureRegion region) {
/*  62 */     if (region == null) throw new IllegalArgumentException("region cannot be null."); 
/*  63 */     this.region = region;
/*     */   }
/*     */   
/*     */   public TextureRegion getRegion() {
/*  67 */     if (this.region == null) throw new IllegalStateException("Region has not been set: " + this); 
/*  68 */     return this.region;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateUVs() {
/*  76 */     float u, v, width, height, regionUVs[] = this.regionUVs;
/*  77 */     if (this.uvs == null || this.uvs.length != regionUVs.length) this.uvs = new float[regionUVs.length]; 
/*  78 */     float[] uvs = this.uvs;
/*  79 */     int n = uvs.length;
/*     */     
/*  81 */     if (this.region instanceof TextureAtlas.AtlasRegion) {
/*  82 */       u = this.region.getU();
/*  83 */       v = this.region.getV();
/*  84 */       TextureAtlas.AtlasRegion region = (TextureAtlas.AtlasRegion)this.region;
/*  85 */       float textureWidth = region.getTexture().getWidth(), textureHeight = region.getTexture().getHeight();
/*  86 */       if (region.rotate) {
/*  87 */         u -= (region.originalHeight - region.offsetY - region.packedWidth) / textureWidth;
/*  88 */         v -= (region.originalWidth - region.offsetX - region.packedHeight) / textureHeight;
/*  89 */         width = region.originalHeight / textureWidth;
/*  90 */         height = region.originalWidth / textureHeight;
/*  91 */         for (int j = 0; j < n; j += 2) {
/*  92 */           uvs[j] = u + regionUVs[j + 1] * width;
/*  93 */           uvs[j + 1] = v + (1.0F - regionUVs[j]) * height;
/*     */         } 
/*     */       } else {
/*  96 */         u -= region.offsetX / textureWidth;
/*  97 */         v -= (region.originalHeight - region.offsetY - region.packedHeight) / textureHeight;
/*  98 */         width = region.originalWidth / textureWidth;
/*  99 */         height = region.originalHeight / textureHeight;
/*     */       } 
/* 101 */     } else if (this.region == null) {
/* 102 */       u = v = 0.0F;
/* 103 */       width = height = 1.0F;
/*     */     } else {
/* 105 */       u = this.region.getU();
/* 106 */       v = this.region.getV();
/* 107 */       width = this.region.getU2() - u;
/* 108 */       height = this.region.getV2() - v;
/*     */     } 
/* 110 */     for (int i = 0; i < n; i += 2) {
/* 111 */       uvs[i] = u + regionUVs[i] * width;
/* 112 */       uvs[i + 1] = v + regionUVs[i + 1] * height;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public short[] getTriangles() {
/* 120 */     return this.triangles;
/*     */   }
/*     */   
/*     */   public void setTriangles(short[] triangles) {
/* 124 */     this.triangles = triangles;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float[] getRegionUVs() {
/* 131 */     return this.regionUVs;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRegionUVs(float[] regionUVs) {
/* 138 */     this.regionUVs = regionUVs;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float[] getUVs() {
/* 147 */     return this.uvs;
/*     */   }
/*     */   
/*     */   public void setUVs(float[] uvs) {
/* 151 */     this.uvs = uvs;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Color getColor() {
/* 158 */     return this.color;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPath() {
/* 165 */     return this.path;
/*     */   }
/*     */   
/*     */   public void setPath(String path) {
/* 169 */     this.path = path;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getHullLength() {
/* 176 */     return this.hullLength;
/*     */   }
/*     */   
/*     */   public void setHullLength(int hullLength) {
/* 180 */     this.hullLength = hullLength;
/*     */   }
/*     */   
/*     */   public void setEdges(short[] edges) {
/* 184 */     this.edges = edges;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public short[] getEdges() {
/* 192 */     return this.edges;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getWidth() {
/* 199 */     return this.width;
/*     */   }
/*     */   
/*     */   public void setWidth(float width) {
/* 203 */     this.width = width;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getHeight() {
/* 210 */     return this.height;
/*     */   }
/*     */   
/*     */   public void setHeight(float height) {
/* 214 */     this.height = height;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MeshAttachment getParentMesh() {
/* 223 */     return this.parentMesh;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setParentMesh(MeshAttachment parentMesh) {
/* 230 */     this.parentMesh = parentMesh;
/* 231 */     if (parentMesh != null) {
/* 232 */       this.bones = parentMesh.bones;
/* 233 */       this.vertices = parentMesh.vertices;
/* 234 */       this.regionUVs = parentMesh.regionUVs;
/* 235 */       this.triangles = parentMesh.triangles;
/* 236 */       this.hullLength = parentMesh.hullLength;
/* 237 */       this.worldVerticesLength = parentMesh.worldVerticesLength;
/* 238 */       this.edges = parentMesh.edges;
/* 239 */       this.width = parentMesh.width;
/* 240 */       this.height = parentMesh.height;
/*     */     } 
/*     */   }
/*     */   
/*     */   public Attachment copy() {
/* 245 */     if (this.parentMesh != null) return newLinkedMesh();
/*     */     
/* 247 */     MeshAttachment copy = new MeshAttachment(this.name);
/* 248 */     copy.region = this.region;
/* 249 */     copy.path = this.path;
/* 250 */     copy.color.set(this.color);
/*     */     
/* 252 */     copyTo(copy);
/* 253 */     copy.regionUVs = new float[this.regionUVs.length];
/* 254 */     SpineUtils.arraycopy(this.regionUVs, 0, copy.regionUVs, 0, this.regionUVs.length);
/* 255 */     copy.uvs = new float[this.uvs.length];
/* 256 */     SpineUtils.arraycopy(this.uvs, 0, copy.uvs, 0, this.uvs.length);
/* 257 */     copy.triangles = new short[this.triangles.length];
/* 258 */     SpineUtils.arraycopy(this.triangles, 0, copy.triangles, 0, this.triangles.length);
/* 259 */     copy.hullLength = this.hullLength;
/*     */ 
/*     */     
/* 262 */     if (this.edges != null) {
/* 263 */       copy.edges = new short[this.edges.length];
/* 264 */       SpineUtils.arraycopy(this.edges, 0, copy.edges, 0, this.edges.length);
/*     */     } 
/* 266 */     copy.width = this.width;
/* 267 */     copy.height = this.height;
/* 268 */     return copy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MeshAttachment newLinkedMesh() {
/* 275 */     MeshAttachment mesh = new MeshAttachment(this.name);
/* 276 */     mesh.region = this.region;
/* 277 */     mesh.path = this.path;
/* 278 */     mesh.color.set(this.color);
/* 279 */     mesh.deformAttachment = this.deformAttachment;
/* 280 */     mesh.setParentMesh((this.parentMesh != null) ? this.parentMesh : this);
/* 281 */     mesh.updateUVs();
/* 282 */     return mesh;
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\attachments\MeshAttachment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */