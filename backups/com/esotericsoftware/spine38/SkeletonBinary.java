/*     */ package com.esotericsoftware.spine38;
/*     */ 
/*     */ import com.badlogic.gdx.files.FileHandle;
/*     */ import com.badlogic.gdx.graphics.Color;
/*     */ import com.badlogic.gdx.graphics.g2d.TextureAtlas;
/*     */ import com.badlogic.gdx.utils.Array;
/*     */ import com.badlogic.gdx.utils.DataInput;
/*     */ import com.badlogic.gdx.utils.FloatArray;
/*     */ import com.badlogic.gdx.utils.IntArray;
/*     */ import com.badlogic.gdx.utils.SerializationException;
/*     */ import com.esotericsoftware.spine38.attachments.AtlasAttachmentLoader;
/*     */ import com.esotericsoftware.spine38.attachments.Attachment;
/*     */ import com.esotericsoftware.spine38.attachments.AttachmentLoader;
/*     */ import com.esotericsoftware.spine38.attachments.AttachmentType;
/*     */ import com.esotericsoftware.spine38.attachments.BoundingBoxAttachment;
/*     */ import com.esotericsoftware.spine38.attachments.ClippingAttachment;
/*     */ import com.esotericsoftware.spine38.attachments.MeshAttachment;
/*     */ import com.esotericsoftware.spine38.attachments.PathAttachment;
/*     */ import com.esotericsoftware.spine38.attachments.PointAttachment;
/*     */ import com.esotericsoftware.spine38.attachments.RegionAttachment;
/*     */ import com.esotericsoftware.spine38.attachments.VertexAttachment;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
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
/*     */ public class SkeletonBinary
/*     */ {
/*     */   public static final int BONE_ROTATE = 0;
/*     */   public static final int BONE_TRANSLATE = 1;
/*     */   public static final int BONE_SCALE = 2;
/*     */   public static final int BONE_SHEAR = 3;
/*     */   public static final int SLOT_ATTACHMENT = 0;
/*     */   public static final int SLOT_COLOR = 1;
/*     */   public static final int SLOT_TWO_COLOR = 2;
/*     */   public static final int PATH_POSITION = 0;
/*     */   public static final int PATH_SPACING = 1;
/*     */   public static final int PATH_MIX = 2;
/*     */   public static final int CURVE_LINEAR = 0;
/*     */   public static final int CURVE_STEPPED = 1;
/*     */   public static final int CURVE_BEZIER = 2;
/* 101 */   private static final Color tempColor1 = new Color(); private static final Color tempColor2 = new Color();
/*     */   
/*     */   private final AttachmentLoader attachmentLoader;
/* 104 */   private float scale = 1.0F;
/* 105 */   private Array<SkeletonJson.LinkedMesh> linkedMeshes = new Array();
/*     */   
/*     */   public SkeletonBinary(TextureAtlas atlas) {
/* 108 */     this.attachmentLoader = (AttachmentLoader)new AtlasAttachmentLoader(atlas);
/*     */   }
/*     */   
/*     */   public SkeletonBinary(AttachmentLoader attachmentLoader) {
/* 112 */     if (attachmentLoader == null) throw new IllegalArgumentException("attachmentLoader cannot be null."); 
/* 113 */     this.attachmentLoader = attachmentLoader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getScale() {
/* 121 */     return this.scale;
/*     */   }
/*     */   
/*     */   public void setScale(float scale) {
/* 125 */     if (scale == 0.0F) throw new IllegalArgumentException("scale cannot be 0."); 
/* 126 */     this.scale = scale;
/*     */   }
/*     */   
/*     */   public SkeletonData readSkeletonData(FileHandle file) {
/* 130 */     if (file == null) throw new IllegalArgumentException("file cannot be null.");
/*     */     
/* 132 */     float scale = this.scale;
/*     */     
/* 134 */     SkeletonData skeletonData = new SkeletonData();
/* 135 */     skeletonData.name = file.nameWithoutExtension();
/*     */     
/* 137 */     SkeletonInput input = new SkeletonInput(file);
/*     */     try {
/* 139 */       skeletonData.hash = input.readString();
/* 140 */       if (skeletonData.hash.isEmpty()) skeletonData.hash = null; 
/* 141 */       skeletonData.version = input.readString();
/* 142 */       if (skeletonData.version.isEmpty()) skeletonData.version = null; 
/* 143 */       skeletonData.x = input.readFloat();
/* 144 */       skeletonData.y = input.readFloat();
/* 145 */       skeletonData.width = input.readFloat();
/* 146 */       skeletonData.height = input.readFloat();
/*     */       
/* 148 */       boolean nonessential = input.readBoolean();
/* 149 */       if (nonessential) {
/* 150 */         skeletonData.fps = input.readFloat();
/*     */         
/* 152 */         skeletonData.imagesPath = input.readString();
/* 153 */         if (skeletonData.imagesPath.isEmpty()) skeletonData.imagesPath = null;
/*     */         
/* 155 */         skeletonData.audioPath = input.readString();
/* 156 */         if (skeletonData.audioPath.isEmpty()) skeletonData.audioPath = null;
/*     */       
/*     */       } 
/*     */ 
/*     */       
/*     */       int n;
/*     */       
/* 163 */       input.strings = new Array(n = input.readInt(true));
/* 164 */       Object[] o = input.strings.setSize(n); int i;
/* 165 */       for (i = 0; i < n; i++) {
/* 166 */         o[i] = input.readString();
/*     */       }
/*     */       
/* 169 */       o = skeletonData.bones.setSize(n = input.readInt(true));
/* 170 */       for (i = 0; i < n; i++) {
/* 171 */         String name = input.readString();
/* 172 */         BoneData parent = (i == 0) ? null : (BoneData)skeletonData.bones.get(input.readInt(true));
/* 173 */         BoneData data = new BoneData(i, name, parent);
/* 174 */         data.rotation = input.readFloat();
/* 175 */         data.x = input.readFloat() * scale;
/* 176 */         data.y = input.readFloat() * scale;
/* 177 */         data.scaleX = input.readFloat();
/* 178 */         data.scaleY = input.readFloat();
/* 179 */         data.shearX = input.readFloat();
/* 180 */         data.shearY = input.readFloat();
/* 181 */         data.length = input.readFloat() * scale;
/* 182 */         data.transformMode = BoneData.TransformMode.values[input.readInt(true)];
/* 183 */         data.skinRequired = input.readBoolean();
/* 184 */         if (nonessential) Color.rgba8888ToColor(data.color, input.readInt()); 
/* 185 */         o[i] = data;
/*     */       } 
/*     */ 
/*     */       
/* 189 */       o = skeletonData.slots.setSize(n = input.readInt(true));
/* 190 */       for (i = 0; i < n; i++) {
/* 191 */         String slotName = input.readString();
/* 192 */         BoneData boneData = (BoneData)skeletonData.bones.get(input.readInt(true));
/* 193 */         SlotData data = new SlotData(i, slotName, boneData);
/* 194 */         Color.rgba8888ToColor(data.color, input.readInt());
/*     */         
/* 196 */         int darkColor = input.readInt();
/* 197 */         if (darkColor != -1) Color.rgb888ToColor(data.darkColor = new Color(), darkColor);
/*     */         
/* 199 */         data.attachmentName = input.readStringRef();
/* 200 */         data.blendMode = BlendMode.values[input.readInt(true)];
/* 201 */         o[i] = data;
/*     */       } 
/*     */ 
/*     */       
/* 205 */       o = skeletonData.ikConstraints.setSize(n = input.readInt(true));
/* 206 */       for (i = 0; i < n; i++) {
/* 207 */         IkConstraintData data = new IkConstraintData(input.readString());
/* 208 */         data.order = input.readInt(true);
/* 209 */         data.skinRequired = input.readBoolean(); int nn;
/* 210 */         Object[] bones = data.bones.setSize(nn = input.readInt(true));
/* 211 */         for (int ii = 0; ii < nn; ii++)
/* 212 */           bones[ii] = skeletonData.bones.get(input.readInt(true)); 
/* 213 */         data.target = (BoneData)skeletonData.bones.get(input.readInt(true));
/* 214 */         data.mix = input.readFloat();
/* 215 */         data.softness = input.readFloat() * scale;
/* 216 */         data.bendDirection = input.readByte();
/* 217 */         data.compress = input.readBoolean();
/* 218 */         data.stretch = input.readBoolean();
/* 219 */         data.uniform = input.readBoolean();
/* 220 */         o[i] = data;
/*     */       } 
/*     */ 
/*     */       
/* 224 */       o = skeletonData.transformConstraints.setSize(n = input.readInt(true));
/* 225 */       for (i = 0; i < n; i++) {
/* 226 */         TransformConstraintData data = new TransformConstraintData(input.readString());
/* 227 */         data.order = input.readInt(true);
/* 228 */         data.skinRequired = input.readBoolean(); int nn;
/* 229 */         Object[] bones = data.bones.setSize(nn = input.readInt(true));
/* 230 */         for (int ii = 0; ii < nn; ii++)
/* 231 */           bones[ii] = skeletonData.bones.get(input.readInt(true)); 
/* 232 */         data.target = (BoneData)skeletonData.bones.get(input.readInt(true));
/* 233 */         data.local = input.readBoolean();
/* 234 */         data.relative = input.readBoolean();
/* 235 */         data.offsetRotation = input.readFloat();
/* 236 */         data.offsetX = input.readFloat() * scale;
/* 237 */         data.offsetY = input.readFloat() * scale;
/* 238 */         data.offsetScaleX = input.readFloat();
/* 239 */         data.offsetScaleY = input.readFloat();
/* 240 */         data.offsetShearY = input.readFloat();
/* 241 */         data.rotateMix = input.readFloat();
/* 242 */         data.translateMix = input.readFloat();
/* 243 */         data.scaleMix = input.readFloat();
/* 244 */         data.shearMix = input.readFloat();
/* 245 */         o[i] = data;
/*     */       } 
/*     */ 
/*     */       
/* 249 */       o = skeletonData.pathConstraints.setSize(n = input.readInt(true));
/* 250 */       for (i = 0; i < n; i++) {
/* 251 */         PathConstraintData data = new PathConstraintData(input.readString());
/* 252 */         data.order = input.readInt(true);
/* 253 */         data.skinRequired = input.readBoolean(); int nn;
/* 254 */         Object[] bones = data.bones.setSize(nn = input.readInt(true));
/* 255 */         for (int ii = 0; ii < nn; ii++)
/* 256 */           bones[ii] = skeletonData.bones.get(input.readInt(true)); 
/* 257 */         data.target = (SlotData)skeletonData.slots.get(input.readInt(true));
/* 258 */         data.positionMode = PathConstraintData.PositionMode.values[input.readInt(true)];
/* 259 */         data.spacingMode = PathConstraintData.SpacingMode.values[input.readInt(true)];
/* 260 */         data.rotateMode = PathConstraintData.RotateMode.values[input.readInt(true)];
/* 261 */         data.offsetRotation = input.readFloat();
/* 262 */         data.position = input.readFloat();
/* 263 */         if (data.positionMode == PathConstraintData.PositionMode.fixed) data.position *= scale; 
/* 264 */         data.spacing = input.readFloat();
/* 265 */         if (data.spacingMode == PathConstraintData.SpacingMode.length || data.spacingMode == PathConstraintData.SpacingMode.fixed) data.spacing *= scale; 
/* 266 */         data.rotateMix = input.readFloat();
/* 267 */         data.translateMix = input.readFloat();
/* 268 */         o[i] = data;
/*     */       } 
/*     */ 
/*     */       
/* 272 */       Skin defaultSkin = readSkin(input, skeletonData, true, nonessential);
/* 273 */       if (defaultSkin != null) {
/* 274 */         skeletonData.defaultSkin = defaultSkin;
/* 275 */         skeletonData.skins.add(defaultSkin);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 280 */       int j = skeletonData.skins.size;
/* 281 */       o = skeletonData.skins.setSize(n = j + input.readInt(true));
/* 282 */       for (; j < n; j++) {
/* 283 */         o[j] = readSkin(input, skeletonData, false, nonessential);
/*     */       }
/*     */ 
/*     */       
/* 287 */       n = this.linkedMeshes.size;
/* 288 */       for (j = 0; j < n; j++) {
/* 289 */         SkeletonJson.LinkedMesh linkedMesh = (SkeletonJson.LinkedMesh)this.linkedMeshes.get(j);
/* 290 */         Skin skin = (linkedMesh.skin == null) ? skeletonData.getDefaultSkin() : skeletonData.findSkin(linkedMesh.skin);
/* 291 */         if (skin == null) throw new SerializationException("Skin not found: " + linkedMesh.skin); 
/* 292 */         Attachment parent = skin.getAttachment(linkedMesh.slotIndex, linkedMesh.parent);
/* 293 */         if (parent == null) throw new SerializationException("Parent mesh not found: " + linkedMesh.parent); 
/* 294 */         linkedMesh.mesh.setDeformAttachment(linkedMesh.inheritDeform ? (VertexAttachment)parent : (VertexAttachment)linkedMesh.mesh);
/* 295 */         linkedMesh.mesh.setParentMesh((MeshAttachment)parent);
/* 296 */         linkedMesh.mesh.updateUVs();
/*     */       } 
/* 298 */       this.linkedMeshes.clear();
/*     */ 
/*     */       
/* 301 */       o = skeletonData.events.setSize(n = input.readInt(true));
/* 302 */       for (j = 0; j < n; j++) {
/* 303 */         EventData data = new EventData(input.readStringRef());
/* 304 */         data.intValue = input.readInt(false);
/* 305 */         data.floatValue = input.readFloat();
/* 306 */         data.stringValue = input.readString();
/* 307 */         data.audioPath = input.readString();
/* 308 */         if (data.audioPath != null) {
/* 309 */           data.volume = input.readFloat();
/* 310 */           data.balance = input.readFloat();
/*     */         } 
/* 312 */         o[j] = data;
/*     */       } 
/*     */ 
/*     */       
/* 316 */       o = skeletonData.animations.setSize(n = input.readInt(true));
/* 317 */       for (j = 0; j < n; j++) {
/* 318 */         o[j] = readAnimation(input, input.readString(), skeletonData);
/*     */       }
/* 320 */     } catch (IOException ex) {
/* 321 */       throw new SerializationException("Error reading skeleton file.", ex);
/*     */     } finally {
/*     */       try {
/* 324 */         input.close();
/* 325 */       } catch (IOException iOException) {}
/*     */     } 
/*     */     
/* 328 */     return skeletonData;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Skin readSkin(SkeletonInput input, SkeletonData skeletonData, boolean defaultSkin, boolean nonessential) throws IOException {
/*     */     Skin skin;
/*     */     int slotCount;
/* 337 */     if (defaultSkin) {
/* 338 */       slotCount = input.readInt(true);
/* 339 */       if (slotCount == 0) return null; 
/* 340 */       skin = new Skin("default");
/*     */     } else {
/* 342 */       skin = new Skin(input.readStringRef());
/* 343 */       Object[] bones = skin.bones.setSize(input.readInt(true)); int j, n;
/* 344 */       for (j = 0, n = skin.bones.size; j < n; j++) {
/* 345 */         bones[j] = skeletonData.bones.get(input.readInt(true));
/*     */       }
/* 347 */       for (j = 0, n = input.readInt(true); j < n; j++)
/* 348 */         skin.constraints.add(skeletonData.ikConstraints.get(input.readInt(true))); 
/* 349 */       for (j = 0, n = input.readInt(true); j < n; j++)
/* 350 */         skin.constraints.add(skeletonData.transformConstraints.get(input.readInt(true))); 
/* 351 */       for (j = 0, n = input.readInt(true); j < n; j++)
/* 352 */         skin.constraints.add(skeletonData.pathConstraints.get(input.readInt(true))); 
/* 353 */       skin.constraints.shrink();
/*     */       
/* 355 */       slotCount = input.readInt(true);
/*     */     } 
/*     */     
/* 358 */     for (int i = 0; i < slotCount; i++) {
/* 359 */       int slotIndex = input.readInt(true);
/* 360 */       for (int ii = 0, nn = input.readInt(true); ii < nn; ii++) {
/* 361 */         String name = input.readStringRef();
/* 362 */         Attachment attachment = readAttachment(input, skeletonData, skin, slotIndex, name, nonessential);
/* 363 */         if (attachment != null) skin.setAttachment(slotIndex, name, attachment); 
/*     */       } 
/*     */     } 
/* 366 */     return skin; } private Attachment readAttachment(SkeletonInput input, SkeletonData skeletonData, Skin skin, int slotIndex, String attachmentName, boolean nonessential) throws IOException { String str1; int vertexCount; String path; boolean closed; float rotation; int endSlotIndex; float f1; Vertices vertices; int color; boolean constantSpeed; float x; int j; float f2; int i1, m; String skinName; int k; float y; Vertices vertices1; float f3; BoundingBoxAttachment box; float[] uvs; String parent; Vertices vertices2; int i2; float scaleX; short[] triangles; boolean inheritDeform; float[] lengths; PointAttachment point; ClippingAttachment clip; float scaleY; Vertices vertices3; float width; int i, i3; float f4; int hullLength; float height; int n; PathAttachment pathAttachment; float f5; short[] edges; MeshAttachment mesh; int i4;
/*     */     float f6;
/*     */     RegionAttachment region;
/*     */     float f7;
/*     */     MeshAttachment meshAttachment1;
/* 371 */     float scale = this.scale;
/*     */     
/* 373 */     String name = input.readStringRef();
/* 374 */     if (name == null) name = attachmentName;
/*     */     
/* 376 */     AttachmentType type = AttachmentType.values[input.readByte()];
/* 377 */     switch (type) {
/*     */       case region:
/* 379 */         str1 = input.readStringRef();
/* 380 */         f1 = input.readFloat();
/* 381 */         f2 = input.readFloat();
/* 382 */         f3 = input.readFloat();
/* 383 */         scaleX = input.readFloat();
/* 384 */         scaleY = input.readFloat();
/* 385 */         f4 = input.readFloat();
/* 386 */         f5 = input.readFloat();
/* 387 */         i4 = input.readInt();
/*     */         
/* 389 */         if (str1 == null) str1 = name; 
/* 390 */         region = this.attachmentLoader.newRegionAttachment(skin, name, str1);
/* 391 */         if (region == null) return null; 
/* 392 */         region.setPath(str1);
/* 393 */         region.setX(f2 * scale);
/* 394 */         region.setY(f3 * scale);
/* 395 */         region.setScaleX(scaleX);
/* 396 */         region.setScaleY(scaleY);
/* 397 */         region.setRotation(f1);
/* 398 */         region.setWidth(f4 * scale);
/* 399 */         region.setHeight(f5 * scale);
/* 400 */         Color.rgba8888ToColor(region.getColor(), i4);
/* 401 */         region.updateOffset();
/* 402 */         return (Attachment)region;
/*     */       
/*     */       case boundingbox:
/* 405 */         vertexCount = input.readInt(true);
/* 406 */         vertices = readVertices(input, vertexCount);
/* 407 */         i1 = nonessential ? input.readInt() : 0;
/*     */         
/* 409 */         box = this.attachmentLoader.newBoundingBoxAttachment(skin, name);
/* 410 */         if (box == null) return null; 
/* 411 */         box.setWorldVerticesLength(vertexCount << 1);
/* 412 */         box.setVertices(vertices.vertices);
/* 413 */         box.setBones(vertices.bones);
/* 414 */         if (nonessential) Color.rgba8888ToColor(box.getColor(), i1); 
/* 415 */         return (Attachment)box;
/*     */       
/*     */       case mesh:
/* 418 */         path = input.readStringRef();
/* 419 */         color = input.readInt();
/* 420 */         m = input.readInt(true);
/* 421 */         uvs = readFloatArray(input, m << 1, 1.0F);
/* 422 */         triangles = readShortArray(input);
/* 423 */         vertices3 = readVertices(input, m);
/* 424 */         hullLength = input.readInt(true);
/* 425 */         edges = null;
/* 426 */         f6 = 0.0F; f7 = 0.0F;
/* 427 */         if (nonessential) {
/* 428 */           edges = readShortArray(input);
/* 429 */           f6 = input.readFloat();
/* 430 */           f7 = input.readFloat();
/*     */         } 
/*     */         
/* 433 */         if (path == null) path = name; 
/* 434 */         meshAttachment1 = this.attachmentLoader.newMeshAttachment(skin, name, path);
/* 435 */         if (meshAttachment1 == null) return null; 
/* 436 */         meshAttachment1.setPath(path);
/* 437 */         Color.rgba8888ToColor(meshAttachment1.getColor(), color);
/* 438 */         meshAttachment1.setBones(vertices3.bones);
/* 439 */         meshAttachment1.setVertices(vertices3.vertices);
/* 440 */         meshAttachment1.setWorldVerticesLength(m << 1);
/* 441 */         meshAttachment1.setTriangles(triangles);
/* 442 */         meshAttachment1.setRegionUVs(uvs);
/* 443 */         meshAttachment1.updateUVs();
/* 444 */         meshAttachment1.setHullLength(hullLength << 1);
/* 445 */         if (nonessential) {
/* 446 */           meshAttachment1.setEdges(edges);
/* 447 */           meshAttachment1.setWidth(f6 * scale);
/* 448 */           meshAttachment1.setHeight(f7 * scale);
/*     */         } 
/* 450 */         return (Attachment)meshAttachment1;
/*     */       
/*     */       case linkedmesh:
/* 453 */         path = input.readStringRef();
/* 454 */         color = input.readInt();
/* 455 */         skinName = input.readStringRef();
/* 456 */         parent = input.readStringRef();
/* 457 */         inheritDeform = input.readBoolean();
/* 458 */         width = 0.0F; height = 0.0F;
/* 459 */         if (nonessential) {
/* 460 */           width = input.readFloat();
/* 461 */           height = input.readFloat();
/*     */         } 
/*     */         
/* 464 */         if (path == null) path = name; 
/* 465 */         mesh = this.attachmentLoader.newMeshAttachment(skin, name, path);
/* 466 */         if (mesh == null) return null; 
/* 467 */         mesh.setPath(path);
/* 468 */         Color.rgba8888ToColor(mesh.getColor(), color);
/* 469 */         if (nonessential) {
/* 470 */           mesh.setWidth(width * scale);
/* 471 */           mesh.setHeight(height * scale);
/*     */         } 
/* 473 */         this.linkedMeshes.add(new SkeletonJson.LinkedMesh(mesh, skinName, slotIndex, parent, inheritDeform));
/* 474 */         return (Attachment)mesh;
/*     */       
/*     */       case path:
/* 477 */         closed = input.readBoolean();
/* 478 */         constantSpeed = input.readBoolean();
/* 479 */         k = input.readInt(true);
/* 480 */         vertices2 = readVertices(input, k);
/* 481 */         lengths = new float[k / 3];
/* 482 */         for (i = 0, n = lengths.length; i < n; i++)
/* 483 */           lengths[i] = input.readFloat() * scale; 
/* 484 */         i3 = nonessential ? input.readInt() : 0;
/*     */         
/* 486 */         pathAttachment = this.attachmentLoader.newPathAttachment(skin, name);
/* 487 */         if (pathAttachment == null) return null; 
/* 488 */         pathAttachment.setClosed(closed);
/* 489 */         pathAttachment.setConstantSpeed(constantSpeed);
/* 490 */         pathAttachment.setWorldVerticesLength(k << 1);
/* 491 */         pathAttachment.setVertices(vertices2.vertices);
/* 492 */         pathAttachment.setBones(vertices2.bones);
/* 493 */         pathAttachment.setLengths(lengths);
/* 494 */         if (nonessential) Color.rgba8888ToColor(pathAttachment.getColor(), i3); 
/* 495 */         return (Attachment)pathAttachment;
/*     */       
/*     */       case point:
/* 498 */         rotation = input.readFloat();
/* 499 */         x = input.readFloat();
/* 500 */         y = input.readFloat();
/* 501 */         i2 = nonessential ? input.readInt() : 0;
/*     */         
/* 503 */         point = this.attachmentLoader.newPointAttachment(skin, name);
/* 504 */         if (point == null) return null; 
/* 505 */         point.setX(x * scale);
/* 506 */         point.setY(y * scale);
/* 507 */         point.setRotation(rotation);
/* 508 */         if (nonessential) Color.rgba8888ToColor(point.getColor(), i2); 
/* 509 */         return (Attachment)point;
/*     */       
/*     */       case clipping:
/* 512 */         endSlotIndex = input.readInt(true);
/* 513 */         j = input.readInt(true);
/* 514 */         vertices1 = readVertices(input, j);
/* 515 */         i2 = nonessential ? input.readInt() : 0;
/*     */         
/* 517 */         clip = this.attachmentLoader.newClippingAttachment(skin, name);
/* 518 */         if (clip == null) return null; 
/* 519 */         clip.setEndSlot((SlotData)skeletonData.slots.get(endSlotIndex));
/* 520 */         clip.setWorldVerticesLength(j << 1);
/* 521 */         clip.setVertices(vertices1.vertices);
/* 522 */         clip.setBones(vertices1.bones);
/* 523 */         if (nonessential) Color.rgba8888ToColor(clip.getColor(), i2); 
/* 524 */         return (Attachment)clip;
/*     */     } 
/*     */     
/* 527 */     return null; }
/*     */ 
/*     */   
/*     */   private Vertices readVertices(SkeletonInput input, int vertexCount) throws IOException {
/* 531 */     int verticesLength = vertexCount << 1;
/* 532 */     Vertices vertices = new Vertices();
/* 533 */     if (!input.readBoolean()) {
/* 534 */       vertices.vertices = readFloatArray(input, verticesLength, this.scale);
/* 535 */       return vertices;
/*     */     } 
/* 537 */     FloatArray weights = new FloatArray(verticesLength * 3 * 3);
/* 538 */     IntArray bonesArray = new IntArray(verticesLength * 3);
/* 539 */     for (int i = 0; i < vertexCount; i++) {
/* 540 */       int boneCount = input.readInt(true);
/* 541 */       bonesArray.add(boneCount);
/* 542 */       for (int ii = 0; ii < boneCount; ii++) {
/* 543 */         bonesArray.add(input.readInt(true));
/* 544 */         weights.add(input.readFloat() * this.scale);
/* 545 */         weights.add(input.readFloat() * this.scale);
/* 546 */         weights.add(input.readFloat());
/*     */       } 
/*     */     } 
/* 549 */     vertices.vertices = weights.toArray();
/* 550 */     vertices.bones = bonesArray.toArray();
/* 551 */     return vertices;
/*     */   }
/*     */   
/*     */   private float[] readFloatArray(SkeletonInput input, int n, float scale) throws IOException {
/* 555 */     float[] array = new float[n];
/* 556 */     if (scale == 1.0F) {
/* 557 */       for (int i = 0; i < n; i++)
/* 558 */         array[i] = input.readFloat(); 
/*     */     } else {
/* 560 */       for (int i = 0; i < n; i++)
/* 561 */         array[i] = input.readFloat() * scale; 
/*     */     } 
/* 563 */     return array;
/*     */   }
/*     */   
/*     */   private short[] readShortArray(SkeletonInput input) throws IOException {
/* 567 */     int n = input.readInt(true);
/* 568 */     short[] array = new short[n];
/* 569 */     for (int i = 0; i < n; i++)
/* 570 */       array[i] = input.readShort(); 
/* 571 */     return array;
/*     */   }
/*     */   
/*     */   private Animation readAnimation(SkeletonInput input, String name, SkeletonData skeletonData) {
/* 575 */     Array<Animation.Timeline> timelines = new Array(32);
/* 576 */     float scale = this.scale;
/* 577 */     float duration = 0.0F;
/*     */     
/*     */     try {
/*     */       int i, n;
/* 581 */       for (i = 0, n = input.readInt(true); i < n; i++) {
/* 582 */         int slotIndex = input.readInt(true);
/* 583 */         for (int ii = 0, nn = input.readInt(true); ii < nn; ii++) {
/* 584 */           Animation.AttachmentTimeline attachmentTimeline; Animation.ColorTimeline colorTimeline; Animation.TwoColorTimeline timeline; int frameIndex, timelineType = input.readByte();
/* 585 */           int frameCount = input.readInt(true);
/* 586 */           switch (timelineType) {
/*     */             case 0:
/* 588 */               attachmentTimeline = new Animation.AttachmentTimeline(frameCount);
/* 589 */               attachmentTimeline.slotIndex = slotIndex;
/* 590 */               for (frameIndex = 0; frameIndex < frameCount; frameIndex++)
/* 591 */                 attachmentTimeline.setFrame(frameIndex, input.readFloat(), input.readStringRef()); 
/* 592 */               timelines.add(attachmentTimeline);
/* 593 */               duration = Math.max(duration, attachmentTimeline.getFrames()[frameCount - 1]);
/*     */               break;
/*     */             
/*     */             case 1:
/* 597 */               colorTimeline = new Animation.ColorTimeline(frameCount);
/* 598 */               colorTimeline.slotIndex = slotIndex;
/* 599 */               for (frameIndex = 0; frameIndex < frameCount; frameIndex++) {
/* 600 */                 float time = input.readFloat();
/* 601 */                 Color.rgba8888ToColor(tempColor1, input.readInt());
/* 602 */                 colorTimeline.setFrame(frameIndex, time, tempColor1.r, tempColor1.g, tempColor1.b, tempColor1.a);
/* 603 */                 if (frameIndex < frameCount - 1) readCurve(input, frameIndex, colorTimeline); 
/*     */               } 
/* 605 */               timelines.add(colorTimeline);
/* 606 */               duration = Math.max(duration, colorTimeline.getFrames()[(frameCount - 1) * 5]);
/*     */               break;
/*     */             
/*     */             case 2:
/* 610 */               timeline = new Animation.TwoColorTimeline(frameCount);
/* 611 */               timeline.slotIndex = slotIndex;
/* 612 */               for (frameIndex = 0; frameIndex < frameCount; frameIndex++) {
/* 613 */                 float time = input.readFloat();
/* 614 */                 Color.rgba8888ToColor(tempColor1, input.readInt());
/* 615 */                 Color.rgb888ToColor(tempColor2, input.readInt());
/* 616 */                 timeline.setFrame(frameIndex, time, tempColor1.r, tempColor1.g, tempColor1.b, tempColor1.a, tempColor2.r, tempColor2.g, tempColor2.b);
/*     */                 
/* 618 */                 if (frameIndex < frameCount - 1) readCurve(input, frameIndex, timeline); 
/*     */               } 
/* 620 */               timelines.add(timeline);
/* 621 */               duration = Math.max(duration, timeline.getFrames()[(frameCount - 1) * 8]);
/*     */               break;
/*     */           } 
/*     */ 
/*     */ 
/*     */         
/*     */         } 
/*     */       } 
/* 629 */       for (i = 0, n = input.readInt(true); i < n; i++) {
/* 630 */         int boneIndex = input.readInt(true);
/* 631 */         for (int ii = 0, nn = input.readInt(true); ii < nn; ii++) {
/* 632 */           Animation.RotateTimeline rotateTimeline; Animation.TranslateTimeline timeline; int frameIndex; float timelineScale; int j, timelineType = input.readByte();
/* 633 */           int frameCount = input.readInt(true);
/* 634 */           switch (timelineType) {
/*     */             case 0:
/* 636 */               rotateTimeline = new Animation.RotateTimeline(frameCount);
/* 637 */               rotateTimeline.boneIndex = boneIndex;
/* 638 */               for (frameIndex = 0; frameIndex < frameCount; frameIndex++) {
/* 639 */                 rotateTimeline.setFrame(frameIndex, input.readFloat(), input.readFloat());
/* 640 */                 if (frameIndex < frameCount - 1) readCurve(input, frameIndex, rotateTimeline); 
/*     */               } 
/* 642 */               timelines.add(rotateTimeline);
/* 643 */               duration = Math.max(duration, rotateTimeline.getFrames()[(frameCount - 1) * 2]);
/*     */               break;
/*     */ 
/*     */             
/*     */             case 1:
/*     */             case 2:
/*     */             case 3:
/* 650 */               timelineScale = 1.0F;
/* 651 */               if (timelineType == 2) {
/* 652 */                 timeline = new Animation.ScaleTimeline(frameCount);
/* 653 */               } else if (timelineType == 3) {
/* 654 */                 timeline = new Animation.ShearTimeline(frameCount);
/*     */               } else {
/* 656 */                 timeline = new Animation.TranslateTimeline(frameCount);
/* 657 */                 timelineScale = scale;
/*     */               } 
/* 659 */               timeline.boneIndex = boneIndex;
/* 660 */               for (j = 0; j < frameCount; j++) {
/* 661 */                 timeline.setFrame(j, input.readFloat(), input.readFloat() * timelineScale, input
/* 662 */                     .readFloat() * timelineScale);
/* 663 */                 if (j < frameCount - 1) readCurve(input, j, timeline); 
/*     */               } 
/* 665 */               timelines.add(timeline);
/* 666 */               duration = Math.max(duration, timeline.getFrames()[(frameCount - 1) * 3]);
/*     */               break;
/*     */           } 
/*     */ 
/*     */ 
/*     */         
/*     */         } 
/*     */       } 
/* 674 */       for (i = 0, n = input.readInt(true); i < n; i++) {
/* 675 */         int index = input.readInt(true);
/* 676 */         int frameCount = input.readInt(true);
/* 677 */         Animation.IkConstraintTimeline timeline = new Animation.IkConstraintTimeline(frameCount);
/* 678 */         timeline.ikConstraintIndex = index;
/* 679 */         for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
/* 680 */           timeline.setFrame(frameIndex, input.readFloat(), input.readFloat(), input.readFloat() * scale, input.readByte(), input
/* 681 */               .readBoolean(), input.readBoolean());
/* 682 */           if (frameIndex < frameCount - 1) readCurve(input, frameIndex, timeline); 
/*     */         } 
/* 684 */         timelines.add(timeline);
/* 685 */         duration = Math.max(duration, timeline.getFrames()[(frameCount - 1) * 6]);
/*     */       } 
/*     */ 
/*     */       
/* 689 */       for (i = 0, n = input.readInt(true); i < n; i++) {
/* 690 */         int index = input.readInt(true);
/* 691 */         int frameCount = input.readInt(true);
/* 692 */         Animation.TransformConstraintTimeline timeline = new Animation.TransformConstraintTimeline(frameCount);
/* 693 */         timeline.transformConstraintIndex = index;
/* 694 */         for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
/* 695 */           timeline.setFrame(frameIndex, input.readFloat(), input.readFloat(), input.readFloat(), input.readFloat(), input
/* 696 */               .readFloat());
/* 697 */           if (frameIndex < frameCount - 1) readCurve(input, frameIndex, timeline); 
/*     */         } 
/* 699 */         timelines.add(timeline);
/* 700 */         duration = Math.max(duration, timeline.getFrames()[(frameCount - 1) * 5]);
/*     */       } 
/*     */ 
/*     */       
/* 704 */       for (i = 0, n = input.readInt(true); i < n; i++) {
/* 705 */         int index = input.readInt(true);
/* 706 */         PathConstraintData data = (PathConstraintData)skeletonData.pathConstraints.get(index);
/* 707 */         for (int ii = 0, nn = input.readInt(true); ii < nn; ii++) {
/* 708 */           Animation.PathConstraintPositionTimeline pathConstraintPositionTimeline; Animation.PathConstraintMixTimeline timeline; float timelineScale; int frameIndex, j, timelineType = input.readByte();
/* 709 */           int frameCount = input.readInt(true);
/* 710 */           switch (timelineType) {
/*     */             
/*     */             case 0:
/*     */             case 1:
/* 714 */               timelineScale = 1.0F;
/* 715 */               if (timelineType == 1) {
/* 716 */                 pathConstraintPositionTimeline = new Animation.PathConstraintSpacingTimeline(frameCount);
/* 717 */                 if (data.spacingMode == PathConstraintData.SpacingMode.length || data.spacingMode == PathConstraintData.SpacingMode.fixed) timelineScale = scale; 
/*     */               } else {
/* 719 */                 pathConstraintPositionTimeline = new Animation.PathConstraintPositionTimeline(frameCount);
/* 720 */                 if (data.positionMode == PathConstraintData.PositionMode.fixed) timelineScale = scale; 
/*     */               } 
/* 722 */               pathConstraintPositionTimeline.pathConstraintIndex = index;
/* 723 */               for (j = 0; j < frameCount; j++) {
/* 724 */                 pathConstraintPositionTimeline.setFrame(j, input.readFloat(), input.readFloat() * timelineScale);
/* 725 */                 if (j < frameCount - 1) readCurve(input, j, pathConstraintPositionTimeline); 
/*     */               } 
/* 727 */               timelines.add(pathConstraintPositionTimeline);
/* 728 */               duration = Math.max(duration, pathConstraintPositionTimeline.getFrames()[(frameCount - 1) * 2]);
/*     */               break;
/*     */             
/*     */             case 2:
/* 732 */               timeline = new Animation.PathConstraintMixTimeline(frameCount);
/* 733 */               timeline.pathConstraintIndex = index;
/* 734 */               for (frameIndex = 0; frameIndex < frameCount; frameIndex++) {
/* 735 */                 timeline.setFrame(frameIndex, input.readFloat(), input.readFloat(), input.readFloat());
/* 736 */                 if (frameIndex < frameCount - 1) readCurve(input, frameIndex, timeline); 
/*     */               } 
/* 738 */               timelines.add(timeline);
/* 739 */               duration = Math.max(duration, timeline.getFrames()[(frameCount - 1) * 3]);
/*     */               break;
/*     */           } 
/*     */ 
/*     */ 
/*     */         
/*     */         } 
/*     */       } 
/* 747 */       for (i = 0, n = input.readInt(true); i < n; i++) {
/* 748 */         Skin skin = (Skin)skeletonData.skins.get(input.readInt(true));
/* 749 */         for (int ii = 0, nn = input.readInt(true); ii < nn; ii++) {
/* 750 */           int slotIndex = input.readInt(true);
/* 751 */           for (int iii = 0, nnn = input.readInt(true); iii < nnn; iii++) {
/* 752 */             VertexAttachment attachment = (VertexAttachment)skin.getAttachment(slotIndex, input.readStringRef());
/* 753 */             boolean weighted = (attachment.getBones() != null);
/* 754 */             float[] vertices = attachment.getVertices();
/* 755 */             int deformLength = weighted ? (vertices.length / 3 * 2) : vertices.length;
/*     */             
/* 757 */             int frameCount = input.readInt(true);
/* 758 */             Animation.DeformTimeline timeline = new Animation.DeformTimeline(frameCount);
/* 759 */             timeline.slotIndex = slotIndex;
/* 760 */             timeline.attachment = attachment;
/*     */             
/* 762 */             for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
/* 763 */               float deform[], time = input.readFloat();
/*     */               
/* 765 */               int end = input.readInt(true);
/* 766 */               if (end == 0) {
/* 767 */                 deform = weighted ? new float[deformLength] : vertices;
/*     */               } else {
/* 769 */                 deform = new float[deformLength];
/* 770 */                 int start = input.readInt(true);
/* 771 */                 end += start;
/* 772 */                 if (scale == 1.0F) {
/* 773 */                   for (int v = start; v < end; v++)
/* 774 */                     deform[v] = input.readFloat(); 
/*     */                 } else {
/* 776 */                   for (int v = start; v < end; v++)
/* 777 */                     deform[v] = input.readFloat() * scale; 
/*     */                 } 
/* 779 */                 if (!weighted) {
/* 780 */                   for (int v = 0, vn = deform.length; v < vn; v++) {
/* 781 */                     deform[v] = deform[v] + vertices[v];
/*     */                   }
/*     */                 }
/*     */               } 
/* 785 */               timeline.setFrame(frameIndex, time, deform);
/* 786 */               if (frameIndex < frameCount - 1) readCurve(input, frameIndex, timeline); 
/*     */             } 
/* 788 */             timelines.add(timeline);
/* 789 */             duration = Math.max(duration, timeline.getFrames()[frameCount - 1]);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 795 */       int drawOrderCount = input.readInt(true);
/* 796 */       if (drawOrderCount > 0) {
/* 797 */         Animation.DrawOrderTimeline timeline = new Animation.DrawOrderTimeline(drawOrderCount);
/* 798 */         int slotCount = skeletonData.slots.size;
/* 799 */         for (int j = 0; j < drawOrderCount; j++) {
/* 800 */           float time = input.readFloat();
/* 801 */           int offsetCount = input.readInt(true);
/* 802 */           int[] drawOrder = new int[slotCount];
/* 803 */           for (int ii = slotCount - 1; ii >= 0; ii--)
/* 804 */             drawOrder[ii] = -1; 
/* 805 */           int[] unchanged = new int[slotCount - offsetCount];
/* 806 */           int originalIndex = 0, unchangedIndex = 0; int k;
/* 807 */           for (k = 0; k < offsetCount; k++) {
/* 808 */             int slotIndex = input.readInt(true);
/*     */             
/* 810 */             while (originalIndex != slotIndex) {
/* 811 */               unchanged[unchangedIndex++] = originalIndex++;
/*     */             }
/* 813 */             drawOrder[originalIndex + input.readInt(true)] = originalIndex++;
/*     */           } 
/*     */           
/* 816 */           while (originalIndex < slotCount) {
/* 817 */             unchanged[unchangedIndex++] = originalIndex++;
/*     */           }
/* 819 */           for (k = slotCount - 1; k >= 0; k--) {
/* 820 */             if (drawOrder[k] == -1) drawOrder[k] = unchanged[--unchangedIndex]; 
/* 821 */           }  timeline.setFrame(j, time, drawOrder);
/*     */         } 
/* 823 */         timelines.add(timeline);
/* 824 */         duration = Math.max(duration, timeline.getFrames()[drawOrderCount - 1]);
/*     */       } 
/*     */ 
/*     */       
/* 828 */       int eventCount = input.readInt(true);
/* 829 */       if (eventCount > 0) {
/* 830 */         Animation.EventTimeline timeline = new Animation.EventTimeline(eventCount);
/* 831 */         for (int j = 0; j < eventCount; j++) {
/* 832 */           float time = input.readFloat();
/* 833 */           EventData eventData = (EventData)skeletonData.events.get(input.readInt(true));
/* 834 */           Event event = new Event(time, eventData);
/* 835 */           event.intValue = input.readInt(false);
/* 836 */           event.floatValue = input.readFloat();
/* 837 */           event.stringValue = input.readBoolean() ? input.readString() : eventData.stringValue;
/* 838 */           if ((event.getData()).audioPath != null) {
/* 839 */             event.volume = input.readFloat();
/* 840 */             event.balance = input.readFloat();
/*     */           } 
/* 842 */           timeline.setFrame(j, event);
/*     */         } 
/* 844 */         timelines.add(timeline);
/* 845 */         duration = Math.max(duration, timeline.getFrames()[eventCount - 1]);
/*     */       } 
/* 847 */     } catch (IOException ex) {
/* 848 */       throw new SerializationException("Error reading skeleton file.", ex);
/*     */     } 
/*     */     
/* 851 */     timelines.shrink();
/* 852 */     return new Animation(name, timelines, duration);
/*     */   }
/*     */   
/*     */   private void readCurve(SkeletonInput input, int frameIndex, Animation.CurveTimeline timeline) throws IOException {
/* 856 */     switch (input.readByte()) {
/*     */       case 1:
/* 858 */         timeline.setStepped(frameIndex);
/*     */         break;
/*     */       case 2:
/* 861 */         setCurve(timeline, frameIndex, input.readFloat(), input.readFloat(), input.readFloat(), input.readFloat());
/*     */         break;
/*     */     } 
/*     */   }
/*     */   
/*     */   void setCurve(Animation.CurveTimeline timeline, int frameIndex, float cx1, float cy1, float cx2, float cy2) {
/* 867 */     timeline.setCurve(frameIndex, cx1, cy1, cx2, cy2);
/*     */   }
/*     */   
/*     */   static class Vertices {
/*     */     int[] bones;
/*     */     float[] vertices;
/*     */   }
/*     */   
/*     */   static class SkeletonInput extends DataInput {
/* 876 */     private char[] chars = new char[32];
/*     */     Array<String> strings;
/*     */     
/*     */     public SkeletonInput(FileHandle file) {
/* 880 */       super(file.read(512));
/*     */     }
/*     */ 
/*     */     
/*     */     public String readStringRef() throws IOException {
/* 885 */       int index = readInt(true);
/* 886 */       return (index == 0) ? null : (String)this.strings.get(index - 1);
/*     */     }
/*     */     
/*     */     public String readString() throws IOException {
/* 890 */       int byteCount = readInt(true);
/* 891 */       switch (byteCount) {
/*     */         case 0:
/* 893 */           return null;
/*     */         case 1:
/* 895 */           return "";
/*     */       } 
/* 897 */       byteCount--;
/* 898 */       if (this.chars.length < byteCount) this.chars = new char[byteCount]; 
/* 899 */       char[] chars = this.chars;
/* 900 */       int charCount = 0;
/* 901 */       for (int i = 0; i < byteCount; ) {
/* 902 */         int b = read();
/* 903 */         switch (b >> 4) {
/*     */           case -1:
/* 905 */             throw new EOFException();
/*     */           case 12:
/*     */           case 13:
/* 908 */             chars[charCount++] = (char)((b & 0x1F) << 6 | read() & 0x3F);
/* 909 */             i += 2;
/*     */             continue;
/*     */           case 14:
/* 912 */             chars[charCount++] = (char)((b & 0xF) << 12 | (read() & 0x3F) << 6 | read() & 0x3F);
/* 913 */             i += 3;
/*     */             continue;
/*     */         } 
/* 916 */         chars[charCount++] = (char)b;
/* 917 */         i++;
/*     */       } 
/*     */       
/* 920 */       return new String(chars, 0, charCount);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\SkeletonBinary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */