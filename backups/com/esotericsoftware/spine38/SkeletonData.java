/*     */ package com.esotericsoftware.spine38;
/*     */ 
/*     */ import com.badlogic.gdx.utils.Array;
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
/*     */ public class SkeletonData
/*     */ {
/*     */   String name;
/*  40 */   final Array<BoneData> bones = new Array();
/*  41 */   final Array<SlotData> slots = new Array();
/*  42 */   final Array<Skin> skins = new Array();
/*     */   Skin defaultSkin;
/*  44 */   final Array<EventData> events = new Array();
/*  45 */   final Array<Animation> animations = new Array();
/*  46 */   final Array<IkConstraintData> ikConstraints = new Array();
/*  47 */   final Array<TransformConstraintData> transformConstraints = new Array();
/*  48 */   final Array<PathConstraintData> pathConstraints = new Array();
/*     */   
/*     */   float x;
/*     */   float y;
/*     */   float width;
/*  53 */   float fps = 30.0F;
/*     */   
/*     */   float height;
/*     */   
/*     */   String version;
/*     */   
/*     */   public Array<BoneData> getBones() {
/*  60 */     return this.bones;
/*     */   }
/*     */   String hash;
/*     */   String imagesPath;
/*     */   String audioPath;
/*     */   
/*     */   public BoneData findBone(String boneName) {
/*  67 */     if (boneName == null) throw new IllegalArgumentException("boneName cannot be null."); 
/*  68 */     Array<BoneData> bones = this.bones;
/*  69 */     for (int i = 0, n = bones.size; i < n; i++) {
/*  70 */       BoneData bone = (BoneData)bones.get(i);
/*  71 */       if (bone.name.equals(boneName)) return bone; 
/*     */     } 
/*  73 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Array<SlotData> getSlots() {
/*  80 */     return this.slots;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SlotData findSlot(String slotName) {
/*  87 */     if (slotName == null) throw new IllegalArgumentException("slotName cannot be null."); 
/*  88 */     Array<SlotData> slots = this.slots;
/*  89 */     for (int i = 0, n = slots.size; i < n; i++) {
/*  90 */       SlotData slot = (SlotData)slots.get(i);
/*  91 */       if (slot.name.equals(slotName)) return slot; 
/*     */     } 
/*  93 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Skin getDefaultSkin() {
/* 103 */     return this.defaultSkin;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDefaultSkin(Skin defaultSkin) {
/* 108 */     this.defaultSkin = defaultSkin;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Skin findSkin(String skinName) {
/* 115 */     if (skinName == null) throw new IllegalArgumentException("skinName cannot be null."); 
/* 116 */     for (Skin skin : this.skins) {
/* 117 */       if (skin.name.equals(skinName)) return skin; 
/* 118 */     }  return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Array<Skin> getSkins() {
/* 123 */     return this.skins;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EventData findEvent(String eventDataName) {
/* 132 */     if (eventDataName == null) throw new IllegalArgumentException("eventDataName cannot be null."); 
/* 133 */     for (EventData eventData : this.events) {
/* 134 */       if (eventData.name.equals(eventDataName)) return eventData; 
/* 135 */     }  return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Array<EventData> getEvents() {
/* 140 */     return this.events;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Array<Animation> getAnimations() {
/* 147 */     return this.animations;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Animation findAnimation(String animationName) {
/* 154 */     if (animationName == null) throw new IllegalArgumentException("animationName cannot be null."); 
/* 155 */     Array<Animation> animations = this.animations;
/* 156 */     for (int i = 0, n = animations.size; i < n; i++) {
/* 157 */       Animation animation = (Animation)animations.get(i);
/* 158 */       if (animation.name.equals(animationName)) return animation; 
/*     */     } 
/* 160 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Array<IkConstraintData> getIkConstraints() {
/* 167 */     return this.ikConstraints;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IkConstraintData findIkConstraint(String constraintName) {
/* 174 */     if (constraintName == null) throw new IllegalArgumentException("constraintName cannot be null."); 
/* 175 */     Array<IkConstraintData> ikConstraints = this.ikConstraints;
/* 176 */     for (int i = 0, n = ikConstraints.size; i < n; i++) {
/* 177 */       IkConstraintData constraint = (IkConstraintData)ikConstraints.get(i);
/* 178 */       if (constraint.name.equals(constraintName)) return constraint; 
/*     */     } 
/* 180 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Array<TransformConstraintData> getTransformConstraints() {
/* 187 */     return this.transformConstraints;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TransformConstraintData findTransformConstraint(String constraintName) {
/* 194 */     if (constraintName == null) throw new IllegalArgumentException("constraintName cannot be null."); 
/* 195 */     Array<TransformConstraintData> transformConstraints = this.transformConstraints;
/* 196 */     for (int i = 0, n = transformConstraints.size; i < n; i++) {
/* 197 */       TransformConstraintData constraint = (TransformConstraintData)transformConstraints.get(i);
/* 198 */       if (constraint.name.equals(constraintName)) return constraint; 
/*     */     } 
/* 200 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Array<PathConstraintData> getPathConstraints() {
/* 207 */     return this.pathConstraints;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PathConstraintData findPathConstraint(String constraintName) {
/* 214 */     if (constraintName == null) throw new IllegalArgumentException("constraintName cannot be null."); 
/* 215 */     Array<PathConstraintData> pathConstraints = this.pathConstraints;
/* 216 */     for (int i = 0, n = pathConstraints.size; i < n; i++) {
/* 217 */       PathConstraintData constraint = (PathConstraintData)pathConstraints.get(i);
/* 218 */       if (constraint.name.equals(constraintName)) return constraint; 
/*     */     } 
/* 220 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/* 228 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setName(String name) {
/* 233 */     this.name = name;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getX() {
/* 238 */     return this.x;
/*     */   }
/*     */   
/*     */   public void setX(float x) {
/* 242 */     this.x = x;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getY() {
/* 247 */     return this.y;
/*     */   }
/*     */   
/*     */   public void setY(float y) {
/* 251 */     this.y = y;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getWidth() {
/* 256 */     return this.width;
/*     */   }
/*     */   
/*     */   public void setWidth(float width) {
/* 260 */     this.width = width;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getHeight() {
/* 265 */     return this.height;
/*     */   }
/*     */   
/*     */   public void setHeight(float height) {
/* 269 */     this.height = height;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getVersion() {
/* 274 */     return this.version;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setVersion(String version) {
/* 279 */     this.version = version;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHash() {
/* 285 */     return this.hash;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setHash(String hash) {
/* 290 */     this.hash = hash;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getImagesPath() {
/* 296 */     return this.imagesPath;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setImagesPath(String imagesPath) {
/* 301 */     this.imagesPath = imagesPath;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getAudioPath() {
/* 307 */     return this.audioPath;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAudioPath(String audioPath) {
/* 312 */     this.audioPath = audioPath;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFps() {
/* 317 */     return this.fps;
/*     */   }
/*     */   
/*     */   public void setFps(float fps) {
/* 321 */     this.fps = fps;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 325 */     return (this.name != null) ? this.name : super.toString();
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\SkeletonData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */