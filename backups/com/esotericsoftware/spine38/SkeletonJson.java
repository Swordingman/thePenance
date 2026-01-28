/*     */ package com.esotericsoftware.spine38;
/*     */ 
/*     */ import com.badlogic.gdx.files.FileHandle;
/*     */ import com.badlogic.gdx.graphics.Color;
/*     */ import com.badlogic.gdx.graphics.g2d.TextureAtlas;
/*     */ import com.badlogic.gdx.utils.Array;
/*     */ import com.badlogic.gdx.utils.FloatArray;
/*     */ import com.badlogic.gdx.utils.IntArray;
/*     */ import com.badlogic.gdx.utils.JsonReader;
/*     */ import com.badlogic.gdx.utils.JsonValue;
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
/*     */ public class SkeletonJson
/*     */ {
/*     */   private final AttachmentLoader attachmentLoader;
/*  84 */   private float scale = 1.0F;
/*  85 */   private Array<LinkedMesh> linkedMeshes = new Array();
/*     */   
/*     */   public SkeletonJson(TextureAtlas atlas) {
/*  88 */     this.attachmentLoader = (AttachmentLoader)new AtlasAttachmentLoader(atlas);
/*     */   }
/*     */   
/*     */   public SkeletonJson(AttachmentLoader attachmentLoader) {
/*  92 */     if (attachmentLoader == null) throw new IllegalArgumentException("attachmentLoader cannot be null."); 
/*  93 */     this.attachmentLoader = attachmentLoader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getScale() {
/* 101 */     return this.scale;
/*     */   }
/*     */   
/*     */   public void setScale(float scale) {
/* 105 */     if (scale == 0.0F) throw new IllegalArgumentException("scale cannot be 0."); 
/* 106 */     this.scale = scale;
/*     */   }
/*     */   
/*     */   protected JsonValue parse(FileHandle file) {
/* 110 */     if (file == null) throw new IllegalArgumentException("file cannot be null."); 
/* 111 */     return (new JsonReader()).parse(file);
/*     */   }
/*     */   
/*     */   public SkeletonData readSkeletonData(FileHandle file) {
/* 115 */     if (file == null) throw new IllegalArgumentException("file cannot be null.");
/*     */     
/* 117 */     float scale = this.scale;
/*     */     
/* 119 */     SkeletonData skeletonData = new SkeletonData();
/* 120 */     skeletonData.name = file.nameWithoutExtension();
/*     */     
/* 122 */     JsonValue root = parse(file);
/*     */ 
/*     */     
/* 125 */     JsonValue skeletonMap = root.get("skeleton");
/* 126 */     if (skeletonMap != null) {
/* 127 */       skeletonData.hash = skeletonMap.getString("hash", null);
/* 128 */       skeletonData.version = skeletonMap.getString("spine", null);
/* 129 */       skeletonData.x = skeletonMap.getFloat("x", 0.0F);
/* 130 */       skeletonData.y = skeletonMap.getFloat("y", 0.0F);
/* 131 */       skeletonData.width = skeletonMap.getFloat("width", 0.0F);
/* 132 */       skeletonData.height = skeletonMap.getFloat("height", 0.0F);
/* 133 */       skeletonData.fps = skeletonMap.getFloat("fps", 30.0F);
/* 134 */       skeletonData.imagesPath = skeletonMap.getString("images", null);
/* 135 */       skeletonData.audioPath = skeletonMap.getString("audio", null);
/*     */     } 
/*     */ 
/*     */     
/* 139 */     for (JsonValue boneMap = root.getChild("bones"); boneMap != null; boneMap = boneMap.next) {
/* 140 */       BoneData parent = null;
/* 141 */       String parentName = boneMap.getString("parent", null);
/* 142 */       if (parentName != null) {
/* 143 */         parent = skeletonData.findBone(parentName);
/* 144 */         if (parent == null) throw new SerializationException("Parent bone not found: " + parentName); 
/*     */       } 
/* 146 */       BoneData data = new BoneData(skeletonData.bones.size, boneMap.getString("name"), parent);
/* 147 */       data.length = boneMap.getFloat("length", 0.0F) * scale;
/* 148 */       data.x = boneMap.getFloat("x", 0.0F) * scale;
/* 149 */       data.y = boneMap.getFloat("y", 0.0F) * scale;
/* 150 */       data.rotation = boneMap.getFloat("rotation", 0.0F);
/* 151 */       data.scaleX = boneMap.getFloat("scaleX", 1.0F);
/* 152 */       data.scaleY = boneMap.getFloat("scaleY", 1.0F);
/* 153 */       data.shearX = boneMap.getFloat("shearX", 0.0F);
/* 154 */       data.shearY = boneMap.getFloat("shearY", 0.0F);
/* 155 */       data.transformMode = BoneData.TransformMode.valueOf(boneMap.getString("transform", BoneData.TransformMode.normal.name()));
/* 156 */       data.skinRequired = boneMap.getBoolean("skin", false);
/*     */       
/* 158 */       String color = boneMap.getString("color", null);
/* 159 */       if (color != null) data.getColor().set(Color.valueOf(color));
/*     */       
/* 161 */       skeletonData.bones.add(data);
/*     */     } 
/*     */ 
/*     */     
/* 165 */     for (JsonValue slotMap = root.getChild("slots"); slotMap != null; slotMap = slotMap.next) {
/* 166 */       String slotName = slotMap.getString("name");
/* 167 */       String boneName = slotMap.getString("bone");
/* 168 */       BoneData boneData = skeletonData.findBone(boneName);
/* 169 */       if (boneData == null) throw new SerializationException("Slot bone not found: " + boneName); 
/* 170 */       SlotData data = new SlotData(skeletonData.slots.size, slotName, boneData);
/*     */       
/* 172 */       String color = slotMap.getString("color", null);
/* 173 */       if (color != null) data.getColor().set(Color.valueOf(color));
/*     */       
/* 175 */       String dark = slotMap.getString("dark", null);
/* 176 */       if (dark != null) data.setDarkColor(Color.valueOf(dark));
/*     */       
/* 178 */       data.attachmentName = slotMap.getString("attachment", null);
/* 179 */       data.blendMode = BlendMode.valueOf(slotMap.getString("blend", BlendMode.normal.name()));
/* 180 */       skeletonData.slots.add(data);
/*     */     } 
/*     */     
/*     */     JsonValue constraintMap;
/* 184 */     for (constraintMap = root.getChild("ik"); constraintMap != null; constraintMap = constraintMap.next) {
/* 185 */       IkConstraintData data = new IkConstraintData(constraintMap.getString("name"));
/* 186 */       data.order = constraintMap.getInt("order", 0);
/* 187 */       data.skinRequired = constraintMap.getBoolean("skin", false);
/*     */       
/* 189 */       for (JsonValue entry = constraintMap.getChild("bones"); entry != null; entry = entry.next) {
/* 190 */         BoneData bone = skeletonData.findBone(entry.asString());
/* 191 */         if (bone == null) throw new SerializationException("IK bone not found: " + entry); 
/* 192 */         data.bones.add(bone);
/*     */       } 
/*     */       
/* 195 */       String targetName = constraintMap.getString("target");
/* 196 */       data.target = skeletonData.findBone(targetName);
/* 197 */       if (data.target == null) throw new SerializationException("IK target bone not found: " + targetName);
/*     */       
/* 199 */       data.mix = constraintMap.getFloat("mix", 1.0F);
/* 200 */       data.softness = constraintMap.getFloat("softness", 0.0F) * scale;
/* 201 */       data.bendDirection = constraintMap.getBoolean("bendPositive", true) ? 1 : -1;
/* 202 */       data.compress = constraintMap.getBoolean("compress", false);
/* 203 */       data.stretch = constraintMap.getBoolean("stretch", false);
/* 204 */       data.uniform = constraintMap.getBoolean("uniform", false);
/*     */       
/* 206 */       skeletonData.ikConstraints.add(data);
/*     */     } 
/*     */ 
/*     */     
/* 210 */     for (constraintMap = root.getChild("transform"); constraintMap != null; constraintMap = constraintMap.next) {
/* 211 */       TransformConstraintData data = new TransformConstraintData(constraintMap.getString("name"));
/* 212 */       data.order = constraintMap.getInt("order", 0);
/* 213 */       data.skinRequired = constraintMap.getBoolean("skin", false);
/*     */       
/* 215 */       for (JsonValue entry = constraintMap.getChild("bones"); entry != null; entry = entry.next) {
/* 216 */         BoneData bone = skeletonData.findBone(entry.asString());
/* 217 */         if (bone == null) throw new SerializationException("Transform constraint bone not found: " + entry); 
/* 218 */         data.bones.add(bone);
/*     */       } 
/*     */       
/* 221 */       String targetName = constraintMap.getString("target");
/* 222 */       data.target = skeletonData.findBone(targetName);
/* 223 */       if (data.target == null) throw new SerializationException("Transform constraint target bone not found: " + targetName);
/*     */       
/* 225 */       data.local = constraintMap.getBoolean("local", false);
/* 226 */       data.relative = constraintMap.getBoolean("relative", false);
/*     */       
/* 228 */       data.offsetRotation = constraintMap.getFloat("rotation", 0.0F);
/* 229 */       data.offsetX = constraintMap.getFloat("x", 0.0F) * scale;
/* 230 */       data.offsetY = constraintMap.getFloat("y", 0.0F) * scale;
/* 231 */       data.offsetScaleX = constraintMap.getFloat("scaleX", 0.0F);
/* 232 */       data.offsetScaleY = constraintMap.getFloat("scaleY", 0.0F);
/* 233 */       data.offsetShearY = constraintMap.getFloat("shearY", 0.0F);
/*     */       
/* 235 */       data.rotateMix = constraintMap.getFloat("rotateMix", 1.0F);
/* 236 */       data.translateMix = constraintMap.getFloat("translateMix", 1.0F);
/* 237 */       data.scaleMix = constraintMap.getFloat("scaleMix", 1.0F);
/* 238 */       data.shearMix = constraintMap.getFloat("shearMix", 1.0F);
/*     */       
/* 240 */       skeletonData.transformConstraints.add(data);
/*     */     } 
/*     */ 
/*     */     
/* 244 */     for (constraintMap = root.getChild("path"); constraintMap != null; constraintMap = constraintMap.next) {
/* 245 */       PathConstraintData data = new PathConstraintData(constraintMap.getString("name"));
/* 246 */       data.order = constraintMap.getInt("order", 0);
/* 247 */       data.skinRequired = constraintMap.getBoolean("skin", false);
/*     */       
/* 249 */       for (JsonValue entry = constraintMap.getChild("bones"); entry != null; entry = entry.next) {
/* 250 */         BoneData bone = skeletonData.findBone(entry.asString());
/* 251 */         if (bone == null) throw new SerializationException("Path bone not found: " + entry); 
/* 252 */         data.bones.add(bone);
/*     */       } 
/*     */       
/* 255 */       String targetName = constraintMap.getString("target");
/* 256 */       data.target = skeletonData.findSlot(targetName);
/* 257 */       if (data.target == null) throw new SerializationException("Path target slot not found: " + targetName);
/*     */       
/* 259 */       data.positionMode = PathConstraintData.PositionMode.valueOf(constraintMap.getString("positionMode", "percent"));
/* 260 */       data.spacingMode = PathConstraintData.SpacingMode.valueOf(constraintMap.getString("spacingMode", "length"));
/* 261 */       data.rotateMode = PathConstraintData.RotateMode.valueOf(constraintMap.getString("rotateMode", "tangent"));
/* 262 */       data.offsetRotation = constraintMap.getFloat("rotation", 0.0F);
/* 263 */       data.position = constraintMap.getFloat("position", 0.0F);
/* 264 */       if (data.positionMode == PathConstraintData.PositionMode.fixed) data.position *= scale; 
/* 265 */       data.spacing = constraintMap.getFloat("spacing", 0.0F);
/* 266 */       if (data.spacingMode == PathConstraintData.SpacingMode.length || data.spacingMode == PathConstraintData.SpacingMode.fixed) data.spacing *= scale; 
/* 267 */       data.rotateMix = constraintMap.getFloat("rotateMix", 1.0F);
/* 268 */       data.translateMix = constraintMap.getFloat("translateMix", 1.0F);
/*     */       
/* 270 */       skeletonData.pathConstraints.add(data);
/*     */     } 
/*     */ 
/*     */     
/* 274 */     for (JsonValue skinMap = root.getChild("skins"); skinMap != null; skinMap = skinMap.next) {
/* 275 */       Skin skin = new Skin(skinMap.getString("name")); JsonValue entry;
/* 276 */       for (entry = skinMap.getChild("bones"); entry != null; entry = entry.next) {
/* 277 */         BoneData bone = skeletonData.findBone(entry.asString());
/* 278 */         if (bone == null) throw new SerializationException("Skin bone not found: " + entry); 
/* 279 */         skin.bones.add(bone);
/*     */       } 
/* 281 */       for (entry = skinMap.getChild("ik"); entry != null; entry = entry.next) {
/* 282 */         IkConstraintData constraint = skeletonData.findIkConstraint(entry.asString());
/* 283 */         if (constraint == null) throw new SerializationException("Skin IK constraint not found: " + entry); 
/* 284 */         skin.constraints.add(constraint);
/*     */       } 
/* 286 */       for (entry = skinMap.getChild("transform"); entry != null; entry = entry.next) {
/* 287 */         TransformConstraintData constraint = skeletonData.findTransformConstraint(entry.asString());
/* 288 */         if (constraint == null) throw new SerializationException("Skin transform constraint not found: " + entry); 
/* 289 */         skin.constraints.add(constraint);
/*     */       } 
/* 291 */       for (entry = skinMap.getChild("path"); entry != null; entry = entry.next) {
/* 292 */         PathConstraintData constraint = skeletonData.findPathConstraint(entry.asString());
/* 293 */         if (constraint == null) throw new SerializationException("Skin path constraint not found: " + entry); 
/* 294 */         skin.constraints.add(constraint);
/*     */       } 
/* 296 */       for (JsonValue slotEntry = skinMap.getChild("attachments"); slotEntry != null; slotEntry = slotEntry.next) {
/* 297 */         SlotData slot = skeletonData.findSlot(slotEntry.name);
/* 298 */         if (slot == null) throw new SerializationException("Slot not found: " + slotEntry.name); 
/* 299 */         for (JsonValue jsonValue = slotEntry.child; jsonValue != null; jsonValue = jsonValue.next) {
/*     */           try {
/* 301 */             Attachment attachment = readAttachment(jsonValue, skin, slot.index, jsonValue.name, skeletonData);
/* 302 */             if (attachment != null) skin.setAttachment(slot.index, jsonValue.name, attachment); 
/* 303 */           } catch (Throwable ex) {
/* 304 */             throw new SerializationException("Error reading attachment: " + jsonValue.name + ", skin: " + skin, ex);
/*     */           } 
/*     */         } 
/*     */       } 
/* 308 */       skeletonData.skins.add(skin);
/* 309 */       if (skin.name.equals("default")) skeletonData.defaultSkin = skin;
/*     */     
/*     */     } 
/*     */     
/* 313 */     for (int i = 0, n = this.linkedMeshes.size; i < n; i++) {
/* 314 */       LinkedMesh linkedMesh = (LinkedMesh)this.linkedMeshes.get(i);
/* 315 */       Skin skin = (linkedMesh.skin == null) ? skeletonData.getDefaultSkin() : skeletonData.findSkin(linkedMesh.skin);
/* 316 */       if (skin == null) throw new SerializationException("Skin not found: " + linkedMesh.skin); 
/* 317 */       Attachment parent = skin.getAttachment(linkedMesh.slotIndex, linkedMesh.parent);
/* 318 */       if (parent == null) throw new SerializationException("Parent mesh not found: " + linkedMesh.parent); 
/* 319 */       linkedMesh.mesh.setDeformAttachment(linkedMesh.inheritDeform ? (VertexAttachment)parent : (VertexAttachment)linkedMesh.mesh);
/* 320 */       linkedMesh.mesh.setParentMesh((MeshAttachment)parent);
/* 321 */       linkedMesh.mesh.updateUVs();
/*     */     } 
/* 323 */     this.linkedMeshes.clear();
/*     */ 
/*     */     
/* 326 */     for (JsonValue eventMap = root.getChild("events"); eventMap != null; eventMap = eventMap.next) {
/* 327 */       EventData data = new EventData(eventMap.name);
/* 328 */       data.intValue = eventMap.getInt("int", 0);
/* 329 */       data.floatValue = eventMap.getFloat("float", 0.0F);
/* 330 */       data.stringValue = eventMap.getString("string", "");
/* 331 */       data.audioPath = eventMap.getString("audio", null);
/* 332 */       if (data.audioPath != null) {
/* 333 */         data.volume = eventMap.getFloat("volume", 1.0F);
/* 334 */         data.balance = eventMap.getFloat("balance", 0.0F);
/*     */       } 
/* 336 */       skeletonData.events.add(data);
/*     */     } 
/*     */ 
/*     */     
/* 340 */     for (JsonValue animationMap = root.getChild("animations"); animationMap != null; animationMap = animationMap.next) {
/*     */       try {
/* 342 */         readAnimation(animationMap, animationMap.name, skeletonData);
/* 343 */       } catch (Throwable ex) {
/* 344 */         throw new SerializationException("Error reading animation: " + animationMap.name, ex);
/*     */       } 
/*     */     } 
/*     */     
/* 348 */     skeletonData.bones.shrink();
/* 349 */     skeletonData.slots.shrink();
/* 350 */     skeletonData.skins.shrink();
/* 351 */     skeletonData.events.shrink();
/* 352 */     skeletonData.animations.shrink();
/* 353 */     skeletonData.ikConstraints.shrink();
/* 354 */     return skeletonData; } private Attachment readAttachment(JsonValue map, Skin skin, int slotIndex, String name, SkeletonData skeletonData) { String str2; BoundingBoxAttachment box; String str1; PathAttachment path; PointAttachment point; ClippingAttachment clip; RegionAttachment region; String str3; MeshAttachment mesh; int vertexCount; String color, end, str5; float[] lengths; String str4, parent; int i;
/*     */     float[] uvs;
/*     */     JsonValue curves;
/*     */     String str6;
/* 358 */     float scale = this.scale;
/* 359 */     name = map.getString("name", name);
/*     */     
/* 361 */     String type = map.getString("type", AttachmentType.region.name());
/*     */     
/* 363 */     switch (AttachmentType.valueOf(type)) {
/*     */       case region:
/* 365 */         str2 = map.getString("path", name);
/* 366 */         region = this.attachmentLoader.newRegionAttachment(skin, name, str2);
/* 367 */         if (region == null) return null; 
/* 368 */         region.setPath(str2);
/* 369 */         region.setX(map.getFloat("x", 0.0F) * scale);
/* 370 */         region.setY(map.getFloat("y", 0.0F) * scale);
/* 371 */         region.setScaleX(map.getFloat("scaleX", 1.0F));
/* 372 */         region.setScaleY(map.getFloat("scaleY", 1.0F));
/* 373 */         region.setRotation(map.getFloat("rotation", 0.0F));
/* 374 */         region.setWidth(map.getFloat("width") * scale);
/* 375 */         region.setHeight(map.getFloat("height") * scale);
/*     */         
/* 377 */         str5 = map.getString("color", null);
/* 378 */         if (str5 != null) region.getColor().set(Color.valueOf(str5));
/*     */         
/* 380 */         region.updateOffset();
/* 381 */         return (Attachment)region;
/*     */       
/*     */       case boundingbox:
/* 384 */         box = this.attachmentLoader.newBoundingBoxAttachment(skin, name);
/* 385 */         if (box == null) return null; 
/* 386 */         readVertices(map, (VertexAttachment)box, map.getInt("vertexCount") << 1);
/*     */         
/* 388 */         str3 = map.getString("color", null);
/* 389 */         if (str3 != null) box.getColor().set(Color.valueOf(str3)); 
/* 390 */         return (Attachment)box;
/*     */       
/*     */       case mesh:
/*     */       case linkedmesh:
/* 394 */         str1 = map.getString("path", name);
/* 395 */         mesh = this.attachmentLoader.newMeshAttachment(skin, name, str1);
/* 396 */         if (mesh == null) return null; 
/* 397 */         mesh.setPath(str1);
/*     */         
/* 399 */         str5 = map.getString("color", null);
/* 400 */         if (str5 != null) mesh.getColor().set(Color.valueOf(str5));
/*     */         
/* 402 */         mesh.setWidth(map.getFloat("width", 0.0F) * scale);
/* 403 */         mesh.setHeight(map.getFloat("height", 0.0F) * scale);
/*     */         
/* 405 */         parent = map.getString("parent", null);
/* 406 */         if (parent != null) {
/* 407 */           this.linkedMeshes
/* 408 */             .add(new LinkedMesh(mesh, map.getString("skin", null), slotIndex, parent, map.getBoolean("deform", true)));
/* 409 */           return (Attachment)mesh;
/*     */         } 
/*     */         
/* 412 */         uvs = map.require("uvs").asFloatArray();
/* 413 */         readVertices(map, (VertexAttachment)mesh, uvs.length);
/* 414 */         mesh.setTriangles(map.require("triangles").asShortArray());
/* 415 */         mesh.setRegionUVs(uvs);
/* 416 */         mesh.updateUVs();
/*     */         
/* 418 */         if (map.has("hull")) mesh.setHullLength(map.require("hull").asInt() * 2); 
/* 419 */         if (map.has("edges")) mesh.setEdges(map.require("edges").asShortArray()); 
/* 420 */         return (Attachment)mesh;
/*     */       
/*     */       case path:
/* 423 */         path = this.attachmentLoader.newPathAttachment(skin, name);
/* 424 */         if (path == null) return null; 
/* 425 */         path.setClosed(map.getBoolean("closed", false));
/* 426 */         path.setConstantSpeed(map.getBoolean("constantSpeed", true));
/*     */         
/* 428 */         vertexCount = map.getInt("vertexCount");
/* 429 */         readVertices(map, (VertexAttachment)path, vertexCount << 1);
/*     */         
/* 431 */         lengths = new float[vertexCount / 3];
/* 432 */         i = 0;
/* 433 */         for (curves = (map.require("lengths")).child; curves != null; curves = curves.next)
/* 434 */           lengths[i++] = curves.asFloat() * scale; 
/* 435 */         path.setLengths(lengths);
/*     */         
/* 437 */         str6 = map.getString("color", null);
/* 438 */         if (str6 != null) path.getColor().set(Color.valueOf(str6)); 
/* 439 */         return (Attachment)path;
/*     */       
/*     */       case point:
/* 442 */         point = this.attachmentLoader.newPointAttachment(skin, name);
/* 443 */         if (point == null) return null; 
/* 444 */         point.setX(map.getFloat("x", 0.0F) * scale);
/* 445 */         point.setY(map.getFloat("y", 0.0F) * scale);
/* 446 */         point.setRotation(map.getFloat("rotation", 0.0F));
/*     */         
/* 448 */         color = map.getString("color", null);
/* 449 */         if (color != null) point.getColor().set(Color.valueOf(color)); 
/* 450 */         return (Attachment)point;
/*     */       
/*     */       case clipping:
/* 453 */         clip = this.attachmentLoader.newClippingAttachment(skin, name);
/* 454 */         if (clip == null) return null;
/*     */         
/* 456 */         end = map.getString("end", null);
/* 457 */         if (end != null) {
/* 458 */           SlotData slot = skeletonData.findSlot(end);
/* 459 */           if (slot == null) throw new SerializationException("Clipping end slot not found: " + end); 
/* 460 */           clip.setEndSlot(slot);
/*     */         } 
/*     */         
/* 463 */         readVertices(map, (VertexAttachment)clip, map.getInt("vertexCount") << 1);
/*     */         
/* 465 */         str4 = map.getString("color", null);
/* 466 */         if (str4 != null) clip.getColor().set(Color.valueOf(str4)); 
/* 467 */         return (Attachment)clip;
/*     */     } 
/*     */     
/* 470 */     return null; }
/*     */ 
/*     */   
/*     */   private void readVertices(JsonValue map, VertexAttachment attachment, int verticesLength) {
/* 474 */     attachment.setWorldVerticesLength(verticesLength);
/* 475 */     float[] vertices = map.require("vertices").asFloatArray();
/* 476 */     if (verticesLength == vertices.length) {
/* 477 */       if (this.scale != 1.0F)
/* 478 */         for (int j = 0, k = vertices.length; j < k; j++) {
/* 479 */           vertices[j] = vertices[j] * this.scale;
/*     */         } 
/* 481 */       attachment.setVertices(vertices);
/*     */       return;
/*     */     } 
/* 484 */     FloatArray weights = new FloatArray(verticesLength * 3 * 3);
/* 485 */     IntArray bones = new IntArray(verticesLength * 3);
/* 486 */     for (int i = 0, n = vertices.length; i < n; ) {
/* 487 */       int boneCount = (int)vertices[i++];
/* 488 */       bones.add(boneCount);
/* 489 */       for (int nn = i + boneCount * 4; i < nn; i += 4) {
/* 490 */         bones.add((int)vertices[i]);
/* 491 */         weights.add(vertices[i + 1] * this.scale);
/* 492 */         weights.add(vertices[i + 2] * this.scale);
/* 493 */         weights.add(vertices[i + 3]);
/*     */       } 
/*     */     } 
/* 496 */     attachment.setBones(bones.toArray());
/* 497 */     attachment.setVertices(weights.toArray());
/*     */   }
/*     */   
/*     */   private void readAnimation(JsonValue map, String name, SkeletonData skeletonData) {
/* 501 */     float scale = this.scale;
/* 502 */     Array<Animation.Timeline> timelines = new Array();
/* 503 */     float duration = 0.0F;
/*     */ 
/*     */     
/* 506 */     for (JsonValue slotMap = map.getChild("slots"); slotMap != null; slotMap = slotMap.next) {
/* 507 */       SlotData slot = skeletonData.findSlot(slotMap.name);
/* 508 */       if (slot == null) throw new SerializationException("Slot not found: " + slotMap.name); 
/* 509 */       for (JsonValue timelineMap = slotMap.child; timelineMap != null; timelineMap = timelineMap.next) {
/* 510 */         String timelineName = timelineMap.name;
/* 511 */         if (timelineName.equals("attachment")) {
/* 512 */           Animation.AttachmentTimeline timeline = new Animation.AttachmentTimeline(timelineMap.size);
/* 513 */           timeline.slotIndex = slot.index;
/*     */           
/* 515 */           int frameIndex = 0;
/* 516 */           for (JsonValue valueMap = timelineMap.child; valueMap != null; valueMap = valueMap.next)
/* 517 */             timeline.setFrame(frameIndex++, valueMap.getFloat("time", 0.0F), valueMap.getString("name")); 
/* 518 */           timelines.add(timeline);
/* 519 */           duration = Math.max(duration, timeline.getFrames()[timeline.getFrameCount() - 1]);
/*     */         }
/* 521 */         else if (timelineName.equals("color")) {
/* 522 */           Animation.ColorTimeline timeline = new Animation.ColorTimeline(timelineMap.size);
/* 523 */           timeline.slotIndex = slot.index;
/*     */           
/* 525 */           int frameIndex = 0;
/* 526 */           for (JsonValue valueMap = timelineMap.child; valueMap != null; valueMap = valueMap.next) {
/* 527 */             Color color = Color.valueOf(valueMap.getString("color"));
/* 528 */             timeline.setFrame(frameIndex, valueMap.getFloat("time", 0.0F), color.r, color.g, color.b, color.a);
/* 529 */             readCurve(valueMap, timeline, frameIndex);
/* 530 */             frameIndex++;
/*     */           } 
/* 532 */           timelines.add(timeline);
/* 533 */           duration = Math.max(duration, timeline.getFrames()[(timeline.getFrameCount() - 1) * 5]);
/*     */         }
/* 535 */         else if (timelineName.equals("twoColor")) {
/* 536 */           Animation.TwoColorTimeline timeline = new Animation.TwoColorTimeline(timelineMap.size);
/* 537 */           timeline.slotIndex = slot.index;
/*     */           
/* 539 */           int frameIndex = 0;
/* 540 */           for (JsonValue valueMap = timelineMap.child; valueMap != null; valueMap = valueMap.next) {
/* 541 */             Color light = Color.valueOf(valueMap.getString("light"));
/* 542 */             Color dark = Color.valueOf(valueMap.getString("dark"));
/* 543 */             timeline.setFrame(frameIndex, valueMap.getFloat("time", 0.0F), light.r, light.g, light.b, light.a, dark.r, dark.g, dark.b);
/*     */             
/* 545 */             readCurve(valueMap, timeline, frameIndex);
/* 546 */             frameIndex++;
/*     */           } 
/* 548 */           timelines.add(timeline);
/* 549 */           duration = Math.max(duration, timeline.getFrames()[(timeline.getFrameCount() - 1) * 8]);
/*     */         } else {
/*     */           
/* 552 */           throw new RuntimeException("Invalid timeline type for a slot: " + timelineName + " (" + slotMap.name + ")");
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 557 */     for (JsonValue boneMap = map.getChild("bones"); boneMap != null; boneMap = boneMap.next) {
/* 558 */       BoneData bone = skeletonData.findBone(boneMap.name);
/* 559 */       if (bone == null) throw new SerializationException("Bone not found: " + boneMap.name); 
/* 560 */       for (JsonValue timelineMap = boneMap.child; timelineMap != null; timelineMap = timelineMap.next) {
/* 561 */         String timelineName = timelineMap.name;
/* 562 */         if (timelineName.equals("rotate")) {
/* 563 */           Animation.RotateTimeline timeline = new Animation.RotateTimeline(timelineMap.size);
/* 564 */           timeline.boneIndex = bone.index;
/*     */           
/* 566 */           int frameIndex = 0;
/* 567 */           for (JsonValue valueMap = timelineMap.child; valueMap != null; valueMap = valueMap.next) {
/* 568 */             timeline.setFrame(frameIndex, valueMap.getFloat("time", 0.0F), valueMap.getFloat("angle", 0.0F));
/* 569 */             readCurve(valueMap, timeline, frameIndex);
/* 570 */             frameIndex++;
/*     */           } 
/* 572 */           timelines.add(timeline);
/* 573 */           duration = Math.max(duration, timeline.getFrames()[(timeline.getFrameCount() - 1) * 2]);
/*     */         }
/* 575 */         else if (timelineName.equals("translate") || timelineName.equals("scale") || timelineName.equals("shear")) {
/*     */           Animation.TranslateTimeline timeline;
/* 577 */           float timelineScale = 1.0F, defaultValue = 0.0F;
/* 578 */           if (timelineName.equals("scale")) {
/* 579 */             timeline = new Animation.ScaleTimeline(timelineMap.size);
/* 580 */             defaultValue = 1.0F;
/* 581 */           } else if (timelineName.equals("shear")) {
/* 582 */             timeline = new Animation.ShearTimeline(timelineMap.size);
/*     */           } else {
/* 584 */             timeline = new Animation.TranslateTimeline(timelineMap.size);
/* 585 */             timelineScale = scale;
/*     */           } 
/* 587 */           timeline.boneIndex = bone.index;
/*     */           
/* 589 */           int frameIndex = 0;
/* 590 */           for (JsonValue valueMap = timelineMap.child; valueMap != null; valueMap = valueMap.next) {
/* 591 */             float x = valueMap.getFloat("x", defaultValue), y = valueMap.getFloat("y", defaultValue);
/* 592 */             timeline.setFrame(frameIndex, valueMap.getFloat("time", 0.0F), x * timelineScale, y * timelineScale);
/* 593 */             readCurve(valueMap, timeline, frameIndex);
/* 594 */             frameIndex++;
/*     */           } 
/* 596 */           timelines.add(timeline);
/* 597 */           duration = Math.max(duration, timeline.getFrames()[(timeline.getFrameCount() - 1) * 3]);
/*     */         } else {
/*     */           
/* 600 */           throw new RuntimeException("Invalid timeline type for a bone: " + timelineName + " (" + boneMap.name + ")");
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     JsonValue constraintMap;
/* 605 */     for (constraintMap = map.getChild("ik"); constraintMap != null; constraintMap = constraintMap.next) {
/* 606 */       IkConstraintData constraint = skeletonData.findIkConstraint(constraintMap.name);
/* 607 */       Animation.IkConstraintTimeline timeline = new Animation.IkConstraintTimeline(constraintMap.size);
/* 608 */       timeline.ikConstraintIndex = skeletonData.getIkConstraints().indexOf(constraint, true);
/* 609 */       int frameIndex = 0;
/* 610 */       for (JsonValue valueMap = constraintMap.child; valueMap != null; valueMap = valueMap.next) {
/* 611 */         timeline.setFrame(frameIndex, valueMap.getFloat("time", 0.0F), valueMap.getFloat("mix", 1.0F), valueMap
/* 612 */             .getFloat("softness", 0.0F) * scale, valueMap.getBoolean("bendPositive", true) ? 1 : -1, valueMap
/* 613 */             .getBoolean("compress", false), valueMap.getBoolean("stretch", false));
/* 614 */         readCurve(valueMap, timeline, frameIndex);
/* 615 */         frameIndex++;
/*     */       } 
/* 617 */       timelines.add(timeline);
/* 618 */       duration = Math.max(duration, timeline.getFrames()[(timeline.getFrameCount() - 1) * 6]);
/*     */     } 
/*     */ 
/*     */     
/* 622 */     for (constraintMap = map.getChild("transform"); constraintMap != null; constraintMap = constraintMap.next) {
/* 623 */       TransformConstraintData constraint = skeletonData.findTransformConstraint(constraintMap.name);
/* 624 */       Animation.TransformConstraintTimeline timeline = new Animation.TransformConstraintTimeline(constraintMap.size);
/* 625 */       timeline.transformConstraintIndex = skeletonData.getTransformConstraints().indexOf(constraint, true);
/* 626 */       int frameIndex = 0;
/* 627 */       for (JsonValue valueMap = constraintMap.child; valueMap != null; valueMap = valueMap.next) {
/* 628 */         timeline.setFrame(frameIndex, valueMap.getFloat("time", 0.0F), valueMap.getFloat("rotateMix", 1.0F), valueMap
/* 629 */             .getFloat("translateMix", 1.0F), valueMap.getFloat("scaleMix", 1.0F), valueMap.getFloat("shearMix", 1.0F));
/* 630 */         readCurve(valueMap, timeline, frameIndex);
/* 631 */         frameIndex++;
/*     */       } 
/* 633 */       timelines.add(timeline);
/* 634 */       duration = Math.max(duration, timeline
/* 635 */           .getFrames()[(timeline.getFrameCount() - 1) * 5]);
/*     */     } 
/*     */ 
/*     */     
/* 639 */     for (constraintMap = map.getChild("path"); constraintMap != null; constraintMap = constraintMap.next) {
/* 640 */       PathConstraintData data = skeletonData.findPathConstraint(constraintMap.name);
/* 641 */       if (data == null) throw new SerializationException("Path constraint not found: " + constraintMap.name); 
/* 642 */       int index = skeletonData.pathConstraints.indexOf(data, true);
/* 643 */       for (JsonValue timelineMap = constraintMap.child; timelineMap != null; timelineMap = timelineMap.next) {
/* 644 */         String timelineName = timelineMap.name;
/* 645 */         if (timelineName.equals("position") || timelineName.equals("spacing")) {
/*     */           Animation.PathConstraintPositionTimeline timeline;
/* 647 */           float timelineScale = 1.0F;
/* 648 */           if (timelineName.equals("spacing")) {
/* 649 */             timeline = new Animation.PathConstraintSpacingTimeline(timelineMap.size);
/* 650 */             if (data.spacingMode == PathConstraintData.SpacingMode.length || data.spacingMode == PathConstraintData.SpacingMode.fixed) timelineScale = scale; 
/*     */           } else {
/* 652 */             timeline = new Animation.PathConstraintPositionTimeline(timelineMap.size);
/* 653 */             if (data.positionMode == PathConstraintData.PositionMode.fixed) timelineScale = scale; 
/*     */           } 
/* 655 */           timeline.pathConstraintIndex = index;
/* 656 */           int frameIndex = 0;
/* 657 */           for (JsonValue valueMap = timelineMap.child; valueMap != null; valueMap = valueMap.next) {
/* 658 */             timeline.setFrame(frameIndex, valueMap.getFloat("time", 0.0F), valueMap.getFloat(timelineName, 0.0F) * timelineScale);
/* 659 */             readCurve(valueMap, timeline, frameIndex);
/* 660 */             frameIndex++;
/*     */           } 
/* 662 */           timelines.add(timeline);
/* 663 */           duration = Math.max(duration, timeline
/* 664 */               .getFrames()[(timeline.getFrameCount() - 1) * 2]);
/* 665 */         } else if (timelineName.equals("mix")) {
/* 666 */           Animation.PathConstraintMixTimeline timeline = new Animation.PathConstraintMixTimeline(timelineMap.size);
/* 667 */           timeline.pathConstraintIndex = index;
/* 668 */           int frameIndex = 0;
/* 669 */           for (JsonValue valueMap = timelineMap.child; valueMap != null; valueMap = valueMap.next) {
/* 670 */             timeline.setFrame(frameIndex, valueMap.getFloat("time", 0.0F), valueMap.getFloat("rotateMix", 1.0F), valueMap
/* 671 */                 .getFloat("translateMix", 1.0F));
/* 672 */             readCurve(valueMap, timeline, frameIndex);
/* 673 */             frameIndex++;
/*     */           } 
/* 675 */           timelines.add(timeline);
/* 676 */           duration = Math.max(duration, timeline
/* 677 */               .getFrames()[(timeline.getFrameCount() - 1) * 3]);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 683 */     for (JsonValue deformMap = map.getChild("deform"); deformMap != null; deformMap = deformMap.next) {
/* 684 */       Skin skin = skeletonData.findSkin(deformMap.name);
/* 685 */       if (skin == null) throw new SerializationException("Skin not found: " + deformMap.name); 
/* 686 */       for (JsonValue jsonValue = deformMap.child; jsonValue != null; jsonValue = jsonValue.next) {
/* 687 */         SlotData slot = skeletonData.findSlot(jsonValue.name);
/* 688 */         if (slot == null) throw new SerializationException("Slot not found: " + jsonValue.name); 
/* 689 */         for (JsonValue timelineMap = jsonValue.child; timelineMap != null; timelineMap = timelineMap.next) {
/* 690 */           VertexAttachment attachment = (VertexAttachment)skin.getAttachment(slot.index, timelineMap.name);
/* 691 */           if (attachment == null) throw new SerializationException("Deform attachment not found: " + timelineMap.name); 
/* 692 */           boolean weighted = (attachment.getBones() != null);
/* 693 */           float[] vertices = attachment.getVertices();
/* 694 */           int deformLength = weighted ? (vertices.length / 3 * 2) : vertices.length;
/*     */           
/* 696 */           Animation.DeformTimeline timeline = new Animation.DeformTimeline(timelineMap.size);
/* 697 */           timeline.slotIndex = slot.index;
/* 698 */           timeline.attachment = attachment;
/*     */           
/* 700 */           int frameIndex = 0;
/* 701 */           for (JsonValue valueMap = timelineMap.child; valueMap != null; valueMap = valueMap.next) {
/*     */             float[] deform;
/* 703 */             JsonValue verticesValue = valueMap.get("vertices");
/* 704 */             if (verticesValue == null) {
/* 705 */               deform = weighted ? new float[deformLength] : vertices;
/*     */             } else {
/* 707 */               deform = new float[deformLength];
/* 708 */               int start = valueMap.getInt("offset", 0);
/* 709 */               SpineUtils.arraycopy(verticesValue.asFloatArray(), 0, deform, start, verticesValue.size);
/* 710 */               if (scale != 1.0F)
/* 711 */                 for (int i = start, n = i + verticesValue.size; i < n; i++) {
/* 712 */                   deform[i] = deform[i] * scale;
/*     */                 } 
/* 714 */               if (!weighted) {
/* 715 */                 for (int i = 0; i < deformLength; i++) {
/* 716 */                   deform[i] = deform[i] + vertices[i];
/*     */                 }
/*     */               }
/*     */             } 
/* 720 */             timeline.setFrame(frameIndex, valueMap.getFloat("time", 0.0F), deform);
/* 721 */             readCurve(valueMap, timeline, frameIndex);
/* 722 */             frameIndex++;
/*     */           } 
/* 724 */           timelines.add(timeline);
/* 725 */           duration = Math.max(duration, timeline.getFrames()[timeline.getFrameCount() - 1]);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 731 */     JsonValue drawOrdersMap = map.get("drawOrder");
/* 732 */     if (drawOrdersMap == null) drawOrdersMap = map.get("draworder"); 
/* 733 */     if (drawOrdersMap != null) {
/* 734 */       Animation.DrawOrderTimeline timeline = new Animation.DrawOrderTimeline(drawOrdersMap.size);
/* 735 */       int slotCount = skeletonData.slots.size;
/* 736 */       int frameIndex = 0;
/* 737 */       for (JsonValue drawOrderMap = drawOrdersMap.child; drawOrderMap != null; drawOrderMap = drawOrderMap.next) {
/* 738 */         int[] drawOrder = null;
/* 739 */         JsonValue offsets = drawOrderMap.get("offsets");
/* 740 */         if (offsets != null) {
/* 741 */           drawOrder = new int[slotCount];
/* 742 */           for (int i = slotCount - 1; i >= 0; i--)
/* 743 */             drawOrder[i] = -1; 
/* 744 */           int[] unchanged = new int[slotCount - offsets.size];
/* 745 */           int originalIndex = 0, unchangedIndex = 0;
/* 746 */           for (JsonValue offsetMap = offsets.child; offsetMap != null; offsetMap = offsetMap.next) {
/* 747 */             SlotData slot = skeletonData.findSlot(offsetMap.getString("slot"));
/* 748 */             if (slot == null) throw new SerializationException("Slot not found: " + offsetMap.getString("slot"));
/*     */             
/* 750 */             while (originalIndex != slot.index) {
/* 751 */               unchanged[unchangedIndex++] = originalIndex++;
/*     */             }
/* 753 */             drawOrder[originalIndex + offsetMap.getInt("offset")] = originalIndex++;
/*     */           } 
/*     */           
/* 756 */           while (originalIndex < slotCount) {
/* 757 */             unchanged[unchangedIndex++] = originalIndex++;
/*     */           }
/* 759 */           for (int j = slotCount - 1; j >= 0; j--) {
/* 760 */             if (drawOrder[j] == -1) drawOrder[j] = unchanged[--unchangedIndex]; 
/*     */           } 
/* 762 */         }  timeline.setFrame(frameIndex++, drawOrderMap.getFloat("time", 0.0F), drawOrder);
/*     */       } 
/* 764 */       timelines.add(timeline);
/* 765 */       duration = Math.max(duration, timeline.getFrames()[timeline.getFrameCount() - 1]);
/*     */     } 
/*     */ 
/*     */     
/* 769 */     JsonValue eventsMap = map.get("events");
/* 770 */     if (eventsMap != null) {
/* 771 */       Animation.EventTimeline timeline = new Animation.EventTimeline(eventsMap.size);
/* 772 */       int frameIndex = 0;
/* 773 */       for (JsonValue eventMap = eventsMap.child; eventMap != null; eventMap = eventMap.next) {
/* 774 */         EventData eventData = skeletonData.findEvent(eventMap.getString("name"));
/* 775 */         if (eventData == null) throw new SerializationException("Event not found: " + eventMap.getString("name")); 
/* 776 */         Event event = new Event(eventMap.getFloat("time", 0.0F), eventData);
/* 777 */         event.intValue = eventMap.getInt("int", eventData.intValue);
/* 778 */         event.floatValue = eventMap.getFloat("float", eventData.floatValue);
/* 779 */         event.stringValue = eventMap.getString("string", eventData.stringValue);
/* 780 */         if ((event.getData()).audioPath != null) {
/* 781 */           event.volume = eventMap.getFloat("volume", eventData.volume);
/* 782 */           event.balance = eventMap.getFloat("balance", eventData.balance);
/*     */         } 
/* 784 */         timeline.setFrame(frameIndex++, event);
/*     */       } 
/* 786 */       timelines.add(timeline);
/* 787 */       duration = Math.max(duration, timeline.getFrames()[timeline.getFrameCount() - 1]);
/*     */     } 
/*     */     
/* 790 */     timelines.shrink();
/* 791 */     skeletonData.animations.add(new Animation(name, timelines, duration));
/*     */   }
/*     */   
/*     */   void readCurve(JsonValue map, Animation.CurveTimeline timeline, int frameIndex) {
/* 795 */     JsonValue curve = map.get("curve");
/* 796 */     if (curve == null)
/* 797 */       return;  if (curve.isString()) {
/* 798 */       timeline.setStepped(frameIndex);
/*     */     } else {
/* 800 */       timeline.setCurve(frameIndex, curve.asFloat(), map.getFloat("c2", 0.0F), map.getFloat("c3", 1.0F), map.getFloat("c4", 1.0F));
/*     */     } 
/*     */   }
/*     */   static class LinkedMesh { String parent;
/*     */     String skin;
/*     */     int slotIndex;
/*     */     MeshAttachment mesh;
/*     */     boolean inheritDeform;
/*     */     
/*     */     public LinkedMesh(MeshAttachment mesh, String skin, int slotIndex, String parent, boolean inheritDeform) {
/* 810 */       this.mesh = mesh;
/* 811 */       this.skin = skin;
/* 812 */       this.slotIndex = slotIndex;
/* 813 */       this.parent = parent;
/* 814 */       this.inheritDeform = inheritDeform;
/*     */     } }
/*     */ 
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\SkeletonJson.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */