/*     */ package com.esotericsoftware.spine38;
/*     */ 
/*     */ import com.badlogic.gdx.graphics.Color;
/*     */ import com.badlogic.gdx.math.Vector2;
/*     */ import com.badlogic.gdx.utils.Array;
/*     */ import com.badlogic.gdx.utils.FloatArray;
/*     */ import com.badlogic.gdx.utils.ObjectMap;
/*     */ import com.esotericsoftware.spine38.attachments.Attachment;
/*     */ import com.esotericsoftware.spine38.attachments.MeshAttachment;
/*     */ import com.esotericsoftware.spine38.attachments.PathAttachment;
/*     */ import com.esotericsoftware.spine38.attachments.RegionAttachment;
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
/*     */ public class Skeleton
/*     */ {
/*     */   final SkeletonData data;
/*     */   final Array<Bone> bones;
/*     */   final Array<Slot> slots;
/*     */   Array<Slot> drawOrder;
/*     */   final Array<IkConstraint> ikConstraints;
/*     */   final Array<TransformConstraint> transformConstraints;
/*     */   final Array<PathConstraint> pathConstraints;
/*  57 */   final Array<Updatable> updateCache = new Array();
/*  58 */   final Array<Bone> updateCacheReset = new Array();
/*     */   Skin skin;
/*     */   final Color color;
/*     */   float time;
/*  62 */   float scaleX = 1.0F, scaleY = 1.0F;
/*     */   float x;
/*     */   
/*     */   public Skeleton(SkeletonData data) {
/*  66 */     if (data == null) throw new IllegalArgumentException("data cannot be null."); 
/*  67 */     this.data = data;
/*     */     
/*  69 */     this.bones = new Array(data.bones.size);
/*  70 */     for (BoneData boneData : data.bones) {
/*     */       Bone bone;
/*  72 */       if (boneData.parent == null) {
/*  73 */         bone = new Bone(boneData, this, null);
/*     */       } else {
/*  75 */         Bone parent = (Bone)this.bones.get(boneData.parent.index);
/*  76 */         bone = new Bone(boneData, this, parent);
/*  77 */         parent.children.add(bone);
/*     */       } 
/*  79 */       this.bones.add(bone);
/*     */     } 
/*     */     
/*  82 */     this.slots = new Array(data.slots.size);
/*  83 */     this.drawOrder = new Array(data.slots.size);
/*  84 */     for (SlotData slotData : data.slots) {
/*  85 */       Bone bone = (Bone)this.bones.get(slotData.boneData.index);
/*  86 */       Slot slot = new Slot(slotData, bone);
/*  87 */       this.slots.add(slot);
/*  88 */       this.drawOrder.add(slot);
/*     */     } 
/*     */     
/*  91 */     this.ikConstraints = new Array(data.ikConstraints.size);
/*  92 */     for (IkConstraintData ikConstraintData : data.ikConstraints) {
/*  93 */       this.ikConstraints.add(new IkConstraint(ikConstraintData, this));
/*     */     }
/*  95 */     this.transformConstraints = new Array(data.transformConstraints.size);
/*  96 */     for (TransformConstraintData transformConstraintData : data.transformConstraints) {
/*  97 */       this.transformConstraints.add(new TransformConstraint(transformConstraintData, this));
/*     */     }
/*  99 */     this.pathConstraints = new Array(data.pathConstraints.size);
/* 100 */     for (PathConstraintData pathConstraintData : data.pathConstraints) {
/* 101 */       this.pathConstraints.add(new PathConstraint(pathConstraintData, this));
/*     */     }
/* 103 */     this.color = new Color(1.0F, 1.0F, 1.0F, 1.0F);
/*     */     
/* 105 */     updateCache();
/*     */   }
/*     */   float y;
/*     */   
/*     */   public Skeleton(Skeleton skeleton) {
/* 110 */     if (skeleton == null) throw new IllegalArgumentException("skeleton cannot be null."); 
/* 111 */     this.data = skeleton.data;
/*     */     
/* 113 */     this.bones = new Array(skeleton.bones.size);
/* 114 */     for (Bone bone : skeleton.bones) {
/*     */       Bone newBone;
/* 116 */       if (bone.parent == null) {
/* 117 */         newBone = new Bone(bone, this, null);
/*     */       } else {
/* 119 */         Bone parent = (Bone)this.bones.get(bone.parent.data.index);
/* 120 */         newBone = new Bone(bone, this, parent);
/* 121 */         parent.children.add(newBone);
/*     */       } 
/* 123 */       this.bones.add(newBone);
/*     */     } 
/*     */     
/* 126 */     this.slots = new Array(skeleton.slots.size);
/* 127 */     for (Slot slot : skeleton.slots) {
/* 128 */       Bone bone = (Bone)this.bones.get(slot.bone.data.index);
/* 129 */       this.slots.add(new Slot(slot, bone));
/*     */     } 
/*     */     
/* 132 */     this.drawOrder = new Array(this.slots.size);
/* 133 */     for (Slot slot : skeleton.drawOrder) {
/* 134 */       this.drawOrder.add(this.slots.get(slot.data.index));
/*     */     }
/* 136 */     this.ikConstraints = new Array(skeleton.ikConstraints.size);
/* 137 */     for (IkConstraint ikConstraint : skeleton.ikConstraints) {
/* 138 */       this.ikConstraints.add(new IkConstraint(ikConstraint, this));
/*     */     }
/* 140 */     this.transformConstraints = new Array(skeleton.transformConstraints.size);
/* 141 */     for (TransformConstraint transformConstraint : skeleton.transformConstraints) {
/* 142 */       this.transformConstraints.add(new TransformConstraint(transformConstraint, this));
/*     */     }
/* 144 */     this.pathConstraints = new Array(skeleton.pathConstraints.size);
/* 145 */     for (PathConstraint pathConstraint : skeleton.pathConstraints) {
/* 146 */       this.pathConstraints.add(new PathConstraint(pathConstraint, this));
/*     */     }
/* 148 */     this.skin = skeleton.skin;
/* 149 */     this.color = new Color(skeleton.color);
/* 150 */     this.time = skeleton.time;
/* 151 */     this.scaleX = skeleton.scaleX;
/* 152 */     this.scaleY = skeleton.scaleY;
/*     */     
/* 154 */     updateCache();
/*     */   }
/*     */ 
/*     */ 
/*     */
/* */   public void updateCache() {
    /* 160 */     Array<Updatable> updateCache = this.updateCache;
    /* 161 */     updateCache.clear();
    /* 162 */     this.updateCacheReset.clear();
    /* */
    /* 164 */     int boneCount = this.bones.size;
    /* 165 */     Object[] bones = this.bones.items;
    /* 166 */     for (int i = 0; i < boneCount; i++) {
        /* 167 */       Bone bone = (Bone)bones[i];
        /* 168 */       bone.sorted = bone.data.skinRequired;
        /* 169 */       bone.active = !bone.sorted;
        /* */     }
    /* */
    /* 171 */     if (this.skin != null) {
        /* 172 */       Object[] skinBones = this.skin.bones.items;
        // --- 修正部分 1: 修复 Skin 循环逻辑 ---
        /* 173 */       for (int k = 0, n = this.skin.bones.size; k < n; k++) {
            /* 174 */         Bone bone = (Bone)bones[((BoneData)skinBones[k]).index];
            /* 175 */         do {
                /* 176 */           bone.sorted = false;
                /* 177 */           bone.active = true;
                /* 178 */           bone = bone.parent;
                /* 179 */         } while (bone != null);
            /* */       }
        /* 183 */     }
    /* */
    /* 183 */     int ikCount = this.ikConstraints.size, transformCount = this.transformConstraints.size, pathCount = this.pathConstraints.size;
    /* 184 */     Object[] ikConstraints = this.ikConstraints.items;
    /* 185 */     Object[] transformConstraints = this.transformConstraints.items;
    /* 186 */     Object[] pathConstraints = this.pathConstraints.items;
    /* 187 */     int constraintCount = ikCount + transformCount + pathCount;
    /* */
    // --- 修正部分 2: 修复 Label 跳转逻辑 ---
    outer: // 定义外部循环标签
    /* 189 */     for (int j = 0; j < constraintCount; j++) {
        /* 190 */       for (int ii = 0; ii < ikCount; ii++) {
            /* 191 */         IkConstraint constraint = (IkConstraint)ikConstraints[ii];
            /* 192 */         if (constraint.data.order == j) {
                /* 193 */           sortIkConstraint(constraint);
                /* 194 */           continue outer; // 找到后直接跳到下一次外部循环 (j++)
                /* */         }
            /* */       }
        /* 197 */       for (int ii = 0; ii < transformCount; ii++) {
            /* 198 */         TransformConstraint constraint = (TransformConstraint)transformConstraints[ii];
            /* 199 */         if (constraint.data.order == j) {
                /* 200 */           sortTransformConstraint(constraint);
                /* 201 */           continue outer; // 找到后直接跳到下一次外部循环 (j++)
                /* */         }
            /* */       }
        /* 204 */       for (int ii = 0; ii < pathCount; ii++) {
            /* 205 */         PathConstraint constraint = (PathConstraint)pathConstraints[ii];
            /* 206 */         if (constraint.data.order == j) {
                /* 207 */           sortPathConstraint(constraint);
                /* 208 */           continue outer; // 找到后直接跳到下一次外部循环 (j++)
                /* */         }
            /* */       }
        /* */     }
    /* */
    /* 213 */     for (int j = 0; j < boneCount; j++)
        /* 214 */       sortBone((Bone)bones[j]);
    /* */   }
/*     */   
/*     */   private void sortIkConstraint(IkConstraint constraint) {
/* 218 */     constraint
/* 219 */       .active = (constraint.target.active && (!constraint.data.skinRequired || (this.skin != null && this.skin.constraints.contains(constraint.data, true))));
/* 220 */     if (!constraint.active)
/*     */       return; 
/* 222 */     Bone target = constraint.target;
/* 223 */     sortBone(target);
/*     */     
/* 225 */     Array<Bone> constrained = constraint.bones;
/* 226 */     Bone parent = (Bone)constrained.first();
/* 227 */     sortBone(parent);
/*     */     
/* 229 */     if (constrained.size > 1) {
/* 230 */       Bone child = (Bone)constrained.peek();
/* 231 */       if (!this.updateCache.contains(child, true)) this.updateCacheReset.add(child);
/*     */     
/*     */     } 
/* 234 */     this.updateCache.add(constraint);
/*     */     
/* 236 */     sortReset(parent.children);
/* 237 */     ((Bone)constrained.peek()).sorted = true;
/*     */   }
/*     */   
/*     */   private void sortPathConstraint(PathConstraint constraint) {
/* 241 */     constraint
/* 242 */       .active = (constraint.target.bone.active && (!constraint.data.skinRequired || (this.skin != null && this.skin.constraints.contains(constraint.data, true))));
/* 243 */     if (!constraint.active)
/*     */       return; 
/* 245 */     Slot slot = constraint.target;
/* 246 */     int slotIndex = (slot.getData()).index;
/* 247 */     Bone slotBone = slot.bone;
/* 248 */     if (this.skin != null) sortPathConstraintAttachment(this.skin, slotIndex, slotBone); 
/* 249 */     if (this.data.defaultSkin != null && this.data.defaultSkin != this.skin) {
/* 250 */       sortPathConstraintAttachment(this.data.defaultSkin, slotIndex, slotBone);
/*     */     }
/* 252 */     Attachment attachment = slot.attachment;
/* 253 */     if (attachment instanceof PathAttachment) sortPathConstraintAttachment(attachment, slotBone);
/*     */     
/* 255 */     Array<Bone> constrained = constraint.bones;
/* 256 */     int boneCount = constrained.size; int i;
/* 257 */     for (i = 0; i < boneCount; i++) {
/* 258 */       sortBone((Bone)constrained.get(i));
/*     */     }
/* 260 */     this.updateCache.add(constraint);
/*     */     
/* 262 */     for (i = 0; i < boneCount; i++)
/* 263 */       sortReset(((Bone)constrained.get(i)).children); 
/* 264 */     for (i = 0; i < boneCount; i++)
/* 265 */       ((Bone)constrained.get(i)).sorted = true; 
/*     */   }
/*     */   
/*     */   private void sortTransformConstraint(TransformConstraint constraint) {
/* 269 */     constraint
/* 270 */       .active = (constraint.target.active && (!constraint.data.skinRequired || (this.skin != null && this.skin.constraints.contains(constraint.data, true))));
/* 271 */     if (!constraint.active)
/*     */       return; 
/* 273 */     sortBone(constraint.target);
/*     */     
/* 275 */     Array<Bone> constrained = constraint.bones;
/* 276 */     int boneCount = constrained.size;
/* 277 */     if (constraint.data.local) {
/* 278 */       for (int j = 0; j < boneCount; j++) {
/* 279 */         Bone child = (Bone)constrained.get(j);
/* 280 */         sortBone(child.parent);
/* 281 */         if (!this.updateCache.contains(child, true)) this.updateCacheReset.add(child); 
/*     */       } 
/*     */     } else {
/* 284 */       for (int j = 0; j < boneCount; j++) {
/* 285 */         sortBone((Bone)constrained.get(j));
/*     */       }
/*     */     } 
/* 288 */     this.updateCache.add(constraint);
/*     */     int i;
/* 290 */     for (i = 0; i < boneCount; i++)
/* 291 */       sortReset(((Bone)constrained.get(i)).children); 
/* 292 */     for (i = 0; i < boneCount; i++)
/* 293 */       ((Bone)constrained.get(i)).sorted = true; 
/*     */   }
/*     */   
/*     */   private void sortPathConstraintAttachment(Skin skin, int slotIndex, Bone slotBone) {
/* 297 */     for (ObjectMap.Keys<Skin.SkinEntry> keys = skin.attachments.keys().iterator(); keys.hasNext(); ) { Skin.SkinEntry entry = keys.next();
/* 298 */       if (entry.getSlotIndex() == slotIndex) sortPathConstraintAttachment(entry.getAttachment(), slotBone);  }
/*     */   
/*     */   }
/*     */   private void sortPathConstraintAttachment(Attachment attachment, Bone slotBone) {
/* 302 */     if (!(attachment instanceof PathAttachment))
/* 303 */       return;  int[] pathBones = ((PathAttachment)attachment).getBones();
/* 304 */     if (pathBones == null) {
/* 305 */       sortBone(slotBone);
/*     */     } else {
/* 307 */       Array<Bone> bones = this.bones;
/* 308 */       for (int i = 0, n = pathBones.length; i < n; ) {
/* 309 */         int nn = pathBones[i++];
/* 310 */         nn += i;
/* 311 */         while (i < nn)
/* 312 */           sortBone((Bone)bones.get(pathBones[i++])); 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void sortBone(Bone bone) {
/* 318 */     if (bone.sorted)
/* 319 */       return;  Bone parent = bone.parent;
/* 320 */     if (parent != null) sortBone(parent); 
/* 321 */     bone.sorted = true;
/* 322 */     this.updateCache.add(bone);
/*     */   }
/*     */   
/*     */   private void sortReset(Array<Bone> bones) {
/* 326 */     for (int i = 0, n = bones.size; i < n; i++) {
/* 327 */       Bone bone = (Bone)bones.get(i);
/* 328 */       if (bone.active) {
/* 329 */         if (bone.sorted) sortReset(bone.children); 
/* 330 */         bone.sorted = false;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateWorldTransform() {
/* 342 */     Array<Bone> updateCacheReset = this.updateCacheReset;
/* 343 */     for (int i = 0, n = updateCacheReset.size; i < n; i++) {
/* 344 */       Bone bone = (Bone)updateCacheReset.get(i);
/* 345 */       bone.ax = bone.x;
/* 346 */       bone.ay = bone.y;
/* 347 */       bone.arotation = bone.rotation;
/* 348 */       bone.ascaleX = bone.scaleX;
/* 349 */       bone.ascaleY = bone.scaleY;
/* 350 */       bone.ashearX = bone.shearX;
/* 351 */       bone.ashearY = bone.shearY;
/* 352 */       bone.appliedValid = true;
/*     */     } 
/* 354 */     Array<Updatable> updateCache = this.updateCache;
/* 355 */     for (int j = 0, k = updateCache.size; j < k; j++) {
/* 356 */       ((Updatable)updateCache.get(j)).update();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateWorldTransform(Bone parent) {
/* 365 */     if (parent == null) throw new IllegalArgumentException("parent cannot be null.");
/*     */ 
/*     */ 
/*     */     
/* 369 */     Array<Bone> updateCacheReset = this.updateCacheReset;
/* 370 */     for (int i = 0, n = updateCacheReset.size; i < n; i++) {
/* 371 */       Bone bone = (Bone)updateCacheReset.get(i);
/* 372 */       bone.ax = bone.x;
/* 373 */       bone.ay = bone.y;
/* 374 */       bone.arotation = bone.rotation;
/* 375 */       bone.ascaleX = bone.scaleX;
/* 376 */       bone.ascaleY = bone.scaleY;
/* 377 */       bone.ashearX = bone.shearX;
/* 378 */       bone.ashearY = bone.shearY;
/* 379 */       bone.appliedValid = true;
/*     */     } 
/*     */ 
/*     */     
/* 383 */     Bone rootBone = getRootBone();
/* 384 */     float pa = parent.a, pb = parent.b, pc = parent.c, pd = parent.d;
/* 385 */     rootBone.worldX = pa * this.x + pb * this.y + parent.worldX;
/* 386 */     rootBone.worldY = pc * this.x + pd * this.y + parent.worldY;
/*     */     
/* 388 */     float rotationY = rootBone.rotation + 90.0F + rootBone.shearY;
/* 389 */     float la = SpineUtils.cosDeg(rootBone.rotation + rootBone.shearX) * rootBone.scaleX;
/* 390 */     float lb = SpineUtils.cosDeg(rotationY) * rootBone.scaleY;
/* 391 */     float lc = SpineUtils.sinDeg(rootBone.rotation + rootBone.shearX) * rootBone.scaleX;
/* 392 */     float ld = SpineUtils.sinDeg(rotationY) * rootBone.scaleY;
/* 393 */     rootBone.a = (pa * la + pb * lc) * this.scaleX;
/* 394 */     rootBone.b = (pa * lb + pb * ld) * this.scaleX;
/* 395 */     rootBone.c = (pc * la + pd * lc) * this.scaleY;
/* 396 */     rootBone.d = (pc * lb + pd * ld) * this.scaleY;
/*     */ 
/*     */     
/* 399 */     Array<Updatable> updateCache = this.updateCache;
/* 400 */     for (int j = 0, k = updateCache.size; j < k; j++) {
/* 401 */       Updatable updatable = (Updatable)updateCache.get(j);
/* 402 */       if (updatable != rootBone) updatable.update();
/*     */     
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setToSetupPose() {
/* 408 */     setBonesToSetupPose();
/* 409 */     setSlotsToSetupPose();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBonesToSetupPose() {
/* 414 */     Array<Bone> bones = this.bones;
/* 415 */     for (int i = 0, n = bones.size; i < n; i++) {
/* 416 */       ((Bone)bones.get(i)).setToSetupPose();
/*     */     }
/* 418 */     Array<IkConstraint> ikConstraints = this.ikConstraints;
/* 419 */     for (int j = 0, m = ikConstraints.size; j < m; j++) {
/* 420 */       IkConstraint constraint = (IkConstraint)ikConstraints.get(j);
/* 421 */       constraint.mix = constraint.data.mix;
/* 422 */       constraint.softness = constraint.data.softness;
/* 423 */       constraint.bendDirection = constraint.data.bendDirection;
/* 424 */       constraint.compress = constraint.data.compress;
/* 425 */       constraint.stretch = constraint.data.stretch;
/*     */     } 
/*     */     
/* 428 */     Array<TransformConstraint> transformConstraints = this.transformConstraints;
/* 429 */     for (int k = 0, i2 = transformConstraints.size; k < i2; k++) {
/* 430 */       TransformConstraint constraint = (TransformConstraint)transformConstraints.get(k);
/* 431 */       TransformConstraintData data = constraint.data;
/* 432 */       constraint.rotateMix = data.rotateMix;
/* 433 */       constraint.translateMix = data.translateMix;
/* 434 */       constraint.scaleMix = data.scaleMix;
/* 435 */       constraint.shearMix = data.shearMix;
/*     */     } 
/*     */     
/* 438 */     Array<PathConstraint> pathConstraints = this.pathConstraints;
/* 439 */     for (int i1 = 0, i3 = pathConstraints.size; i1 < i3; i1++) {
/* 440 */       PathConstraint constraint = (PathConstraint)pathConstraints.get(i1);
/* 441 */       PathConstraintData data = constraint.data;
/* 442 */       constraint.position = data.position;
/* 443 */       constraint.spacing = data.spacing;
/* 444 */       constraint.rotateMix = data.rotateMix;
/* 445 */       constraint.translateMix = data.translateMix;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSlotsToSetupPose() {
/* 451 */     Array<Slot> slots = this.slots;
/* 452 */     SpineUtils.arraycopy(slots.items, 0, this.drawOrder.items, 0, slots.size);
/* 453 */     for (int i = 0, n = slots.size; i < n; i++) {
/* 454 */       ((Slot)slots.get(i)).setToSetupPose();
/*     */     }
/*     */   }
/*     */   
/*     */   public SkeletonData getData() {
/* 459 */     return this.data;
/*     */   }
/*     */ 
/*     */   
/*     */   public Array<Bone> getBones() {
/* 464 */     return this.bones;
/*     */   }
/*     */ 
/*     */   
/*     */   public Array<Updatable> getUpdateCache() {
/* 469 */     return this.updateCache;
/*     */   }
/*     */ 
/*     */   
/*     */   public Bone getRootBone() {
/* 474 */     if (this.bones.size == 0) return null; 
/* 475 */     return (Bone)this.bones.first();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Bone findBone(String boneName) {
/* 482 */     if (boneName == null) throw new IllegalArgumentException("boneName cannot be null."); 
/* 483 */     Array<Bone> bones = this.bones;
/* 484 */     for (int i = 0, n = bones.size; i < n; i++) {
/* 485 */       Bone bone = (Bone)bones.get(i);
/* 486 */       if (bone.data.name.equals(boneName)) return bone; 
/*     */     } 
/* 488 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Array<Slot> getSlots() {
/* 493 */     return this.slots;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Slot findSlot(String slotName) {
/* 500 */     if (slotName == null) throw new IllegalArgumentException("slotName cannot be null."); 
/* 501 */     Array<Slot> slots = this.slots;
/* 502 */     for (int i = 0, n = slots.size; i < n; i++) {
/* 503 */       Slot slot = (Slot)slots.get(i);
/* 504 */       if (slot.data.name.equals(slotName)) return slot; 
/*     */     } 
/* 506 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Array<Slot> getDrawOrder() {
/* 511 */     return this.drawOrder;
/*     */   }
/*     */   
/*     */   public void setDrawOrder(Array<Slot> drawOrder) {
/* 515 */     if (drawOrder == null) throw new IllegalArgumentException("drawOrder cannot be null."); 
/* 516 */     this.drawOrder = drawOrder;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Skin getSkin() {
/* 522 */     return this.skin;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSkin(String skinName) {
/* 529 */     Skin skin = this.data.findSkin(skinName);
/* 530 */     if (skin == null) throw new IllegalArgumentException("Skin not found: " + skinName); 
/* 531 */     setSkin(skin);
/*     */   }
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
/*     */   public void setSkin(Skin newSkin) {
/* 545 */     if (newSkin == this.skin)
/* 546 */       return;  if (newSkin != null)
/* 547 */       if (this.skin != null) {
/* 548 */         newSkin.attachAll(this, this.skin);
/*     */       } else {
/* 550 */         Array<Slot> slots = this.slots;
/* 551 */         for (int i = 0, n = slots.size; i < n; i++) {
/* 552 */           Slot slot = (Slot)slots.get(i);
/* 553 */           String name = slot.data.attachmentName;
/* 554 */           if (name != null) {
/* 555 */             Attachment attachment = newSkin.getAttachment(i, name);
/* 556 */             if (attachment != null) slot.setAttachment(attachment);
/*     */           
/*     */           } 
/*     */         } 
/*     */       }  
/* 561 */     this.skin = newSkin;
/* 562 */     updateCache();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Attachment getAttachment(String slotName, String attachmentName) {
/* 571 */     SlotData slot = this.data.findSlot(slotName);
/* 572 */     if (slot == null) throw new IllegalArgumentException("Slot not found: " + slotName); 
/* 573 */     return getAttachment(slot.getIndex(), attachmentName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Attachment getAttachment(int slotIndex, String attachmentName) {
/* 582 */     if (attachmentName == null) throw new IllegalArgumentException("attachmentName cannot be null."); 
/* 583 */     if (this.skin != null) {
/* 584 */       Attachment attachment = this.skin.getAttachment(slotIndex, attachmentName);
/* 585 */       if (attachment != null) return attachment; 
/*     */     } 
/* 587 */     if (this.data.defaultSkin != null) return this.data.defaultSkin.getAttachment(slotIndex, attachmentName); 
/* 588 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAttachment(String slotName, String attachmentName) {
/* 595 */     if (slotName == null) throw new IllegalArgumentException("slotName cannot be null."); 
/* 596 */     Slot slot = findSlot(slotName);
/* 597 */     if (slot == null) throw new IllegalArgumentException("Slot not found: " + slotName); 
/* 598 */     Attachment attachment = null;
/* 599 */     if (attachmentName != null) {
/* 600 */       attachment = getAttachment(slot.data.index, attachmentName);
/* 601 */       if (attachment == null)
/* 602 */         throw new IllegalArgumentException("Attachment not found: " + attachmentName + ", for slot: " + slotName); 
/*     */     } 
/* 604 */     slot.setAttachment(attachment);
/*     */   }
/*     */ 
/*     */   
/*     */   public Array<IkConstraint> getIkConstraints() {
/* 609 */     return this.ikConstraints;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IkConstraint findIkConstraint(String constraintName) {
/* 616 */     if (constraintName == null) throw new IllegalArgumentException("constraintName cannot be null."); 
/* 617 */     Array<IkConstraint> ikConstraints = this.ikConstraints;
/* 618 */     for (int i = 0, n = ikConstraints.size; i < n; i++) {
/* 619 */       IkConstraint ikConstraint = (IkConstraint)ikConstraints.get(i);
/* 620 */       if (ikConstraint.data.name.equals(constraintName)) return ikConstraint; 
/*     */     } 
/* 622 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Array<TransformConstraint> getTransformConstraints() {
/* 627 */     return this.transformConstraints;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TransformConstraint findTransformConstraint(String constraintName) {
/* 634 */     if (constraintName == null) throw new IllegalArgumentException("constraintName cannot be null."); 
/* 635 */     Array<TransformConstraint> transformConstraints = this.transformConstraints;
/* 636 */     for (int i = 0, n = transformConstraints.size; i < n; i++) {
/* 637 */       TransformConstraint constraint = (TransformConstraint)transformConstraints.get(i);
/* 638 */       if (constraint.data.name.equals(constraintName)) return constraint; 
/*     */     } 
/* 640 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Array<PathConstraint> getPathConstraints() {
/* 645 */     return this.pathConstraints;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PathConstraint findPathConstraint(String constraintName) {
/* 652 */     if (constraintName == null) throw new IllegalArgumentException("constraintName cannot be null."); 
/* 653 */     Array<PathConstraint> pathConstraints = this.pathConstraints;
/* 654 */     for (int i = 0, n = pathConstraints.size; i < n; i++) {
/* 655 */       PathConstraint constraint = (PathConstraint)pathConstraints.get(i);
/* 656 */       if (constraint.data.name.equals(constraintName)) return constraint; 
/*     */     } 
/* 658 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void getBounds(Vector2 offset, Vector2 size, FloatArray temp) {
/* 666 */     if (offset == null) throw new IllegalArgumentException("offset cannot be null."); 
/* 667 */     if (size == null) throw new IllegalArgumentException("size cannot be null."); 
/* 668 */     if (temp == null) throw new IllegalArgumentException("temp cannot be null."); 
/* 669 */     Array<Slot> drawOrder = this.drawOrder;
/* 670 */     float minX = 2.14748365E9F, minY = 2.14748365E9F, maxX = -2.14748365E9F, maxY = -2.14748365E9F;
/* 671 */     for (int i = 0, n = drawOrder.size; i < n; i++) {
/* 672 */       Slot slot = (Slot)drawOrder.get(i);
/* 673 */       if (slot.bone.active) {
/* 674 */         int verticesLength = 0;
/* 675 */         float[] vertices = null;
/* 676 */         Attachment attachment = slot.attachment;
/* 677 */         if (attachment instanceof RegionAttachment) {
/* 678 */           verticesLength = 8;
/* 679 */           vertices = temp.setSize(8);
/* 680 */           ((RegionAttachment)attachment).computeWorldVertices(slot.getBone(), vertices, 0, 2);
/* 681 */         } else if (attachment instanceof MeshAttachment) {
/* 682 */           MeshAttachment mesh = (MeshAttachment)attachment;
/* 683 */           verticesLength = mesh.getWorldVerticesLength();
/* 684 */           vertices = temp.setSize(verticesLength);
/* 685 */           mesh.computeWorldVertices(slot, 0, verticesLength, vertices, 0, 2);
/*     */         } 
/* 687 */         if (vertices != null)
/* 688 */           for (int ii = 0; ii < verticesLength; ii += 2) {
/* 689 */             float x = vertices[ii], y = vertices[ii + 1];
/* 690 */             minX = Math.min(minX, x);
/* 691 */             minY = Math.min(minY, y);
/* 692 */             maxX = Math.max(maxX, x);
/* 693 */             maxY = Math.max(maxY, y);
/*     */           }  
/*     */       } 
/*     */     } 
/* 697 */     offset.set(minX, minY);
/* 698 */     size.set(maxX - minX, maxY - minY);
/*     */   }
/*     */ 
/*     */   
/*     */   public Color getColor() {
/* 703 */     return this.color;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setColor(Color color) {
/* 708 */     if (color == null) throw new IllegalArgumentException("color cannot be null."); 
/* 709 */     this.color.set(color);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public float getScaleX() {
/* 715 */     return this.scaleX;
/*     */   }
/*     */   
/*     */   public void setScaleX(float scaleX) {
/* 719 */     this.scaleX = scaleX;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public float getScaleY() {
/* 725 */     return this.scaleY;
/*     */   }
/*     */   
/*     */   public void setScaleY(float scaleY) {
/* 729 */     this.scaleY = scaleY;
/*     */   }
/*     */   
/*     */   public void setScale(float scaleX, float scaleY) {
/* 733 */     this.scaleX = scaleX;
/* 734 */     this.scaleY = scaleY;
/*     */   }
/*     */   
/*     */   public void setFlip(boolean flipX, boolean flipY) {
/* 738 */     this.scaleX = flipX ? -this.scaleX : this.scaleX;
/* 739 */     this.scaleY = flipY ? -this.scaleY : this.scaleY;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getX() {
/* 744 */     return this.x;
/*     */   }
/*     */   
/*     */   public void setX(float x) {
/* 748 */     this.x = x;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getY() {
/* 753 */     return this.y;
/*     */   }
/*     */   
/*     */   public void setY(float y) {
/* 757 */     this.y = y;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPosition(float x, float y) {
/* 762 */     this.x = x;
/* 763 */     this.y = y;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getTime() {
/* 770 */     return this.time;
/*     */   }
/*     */   
/*     */   public void setTime(float time) {
/* 774 */     this.time = time;
/*     */   }
/*     */ 
/*     */   
/*     */   public void update(float delta) {
/* 779 */     this.time += delta;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 783 */     return (this.data.name != null) ? this.data.name : super.toString();
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\Skeleton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */