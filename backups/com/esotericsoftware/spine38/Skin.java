/*     */ package com.esotericsoftware.spine38;
/*     */ 
/*     */ import com.badlogic.gdx.utils.Array;
/*     */ import com.badlogic.gdx.utils.ObjectMap;
/*     */ import com.badlogic.gdx.utils.OrderedMap;
/*     */ import com.esotericsoftware.spine38.attachments.Attachment;
/*     */ import com.esotericsoftware.spine38.attachments.MeshAttachment;
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
/*     */ public class Skin
/*     */ {
/*     */   final String name;
/*  43 */   final OrderedMap<SkinEntry, SkinEntry> attachments = new OrderedMap();
/*  44 */   final Array<BoneData> bones = new Array();
/*  45 */   final Array<ConstraintData> constraints = new Array();
/*  46 */   private final SkinEntry lookup = new SkinEntry();
/*     */   
/*     */   public Skin(String name) {
/*  49 */     if (name == null) throw new IllegalArgumentException("name cannot be null."); 
/*  50 */     this.name = name;
/*  51 */     (this.attachments.orderedKeys()).ordered = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAttachment(int slotIndex, String name, Attachment attachment) {
/*  56 */     if (slotIndex < 0) throw new IllegalArgumentException("slotIndex must be >= 0."); 
/*  57 */     if (attachment == null) throw new IllegalArgumentException("attachment cannot be null."); 
/*  58 */     SkinEntry newEntry = new SkinEntry(slotIndex, name, attachment);
/*  59 */     SkinEntry oldEntry = (SkinEntry)this.attachments.put(newEntry, newEntry);
/*  60 */     if (oldEntry != null) {
/*  61 */       oldEntry.attachment = attachment;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void addSkin(Skin skin) {
/*  67 */     if (skin == null) throw new IllegalArgumentException("skin cannot be null.");
/*     */     
/*  69 */     for (BoneData data : skin.bones) {
/*  70 */       if (!this.bones.contains(data, true)) this.bones.add(data); 
/*     */     } 
/*  72 */     for (ConstraintData data : skin.constraints) {
/*  73 */       if (!this.constraints.contains(data, true)) this.constraints.add(data); 
/*     */     } 
/*  75 */     for (ObjectMap.Keys<SkinEntry> keys = skin.attachments.keys().iterator(); keys.hasNext(); ) { SkinEntry entry = keys.next();
/*  76 */       setAttachment(entry.slotIndex, entry.name, entry.attachment); }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   public void copySkin(Skin skin) {
/*  82 */     if (skin == null) throw new IllegalArgumentException("skin cannot be null.");
/*     */     
/*  84 */     for (BoneData data : skin.bones) {
/*  85 */       if (!this.bones.contains(data, true)) this.bones.add(data); 
/*     */     } 
/*  87 */     for (ConstraintData data : skin.constraints) {
/*  88 */       if (!this.constraints.contains(data, true)) this.constraints.add(data); 
/*     */     } 
/*  90 */     for (ObjectMap.Keys<SkinEntry> keys = skin.attachments.keys().iterator(); keys.hasNext(); ) { SkinEntry entry = keys.next();
/*  91 */       if (entry.attachment instanceof MeshAttachment) {
/*  92 */         setAttachment(entry.slotIndex, entry.name, (Attachment)((MeshAttachment)entry.attachment).newLinkedMesh()); continue;
/*     */       } 
/*  94 */       setAttachment(entry.slotIndex, entry.name, (entry.attachment != null) ? entry.attachment.copy() : null); }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   public Attachment getAttachment(int slotIndex, String name) {
/* 100 */     if (slotIndex < 0) throw new IllegalArgumentException("slotIndex must be >= 0."); 
/* 101 */     this.lookup.set(slotIndex, name);
/* 102 */     SkinEntry entry = (SkinEntry)this.attachments.get(this.lookup);
/* 103 */     return (entry != null) ? entry.attachment : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeAttachment(int slotIndex, String name) {
/* 108 */     if (slotIndex < 0) throw new IllegalArgumentException("slotIndex must be >= 0."); 
/* 109 */     this.lookup.set(slotIndex, name);
/* 110 */     this.attachments.remove(this.lookup);
/*     */   }
/*     */ 
/*     */   
/*     */   public Array<SkinEntry> getAttachments() {
/* 115 */     return this.attachments.orderedKeys();
/*     */   }
/*     */ 
/*     */   
/*     */   public void getAttachments(int slotIndex, Array<SkinEntry> attachments) {
/* 120 */     if (slotIndex < 0) throw new IllegalArgumentException("slotIndex must be >= 0."); 
/* 121 */     if (attachments == null) throw new IllegalArgumentException("attachments cannot be null."); 
/* 122 */     for (ObjectMap.Keys<SkinEntry> keys = this.attachments.keys().iterator(); keys.hasNext(); ) { SkinEntry entry = keys.next();
/* 123 */       if (entry.slotIndex == slotIndex) attachments.add(entry);  }
/*     */   
/*     */   }
/*     */   
/*     */   public void clear() {
/* 128 */     this.attachments.clear(1024);
/* 129 */     this.bones.clear();
/* 130 */     this.constraints.clear();
/*     */   }
/*     */   
/*     */   public Array<BoneData> getBones() {
/* 134 */     return this.bones;
/*     */   }
/*     */   
/*     */   public Array<ConstraintData> getConstraints() {
/* 138 */     return this.constraints;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 143 */     return this.name;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 147 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   void attachAll(Skeleton skeleton, Skin oldSkin) {
/* 152 */     for (ObjectMap.Keys<SkinEntry> keys = oldSkin.attachments.keys().iterator(); keys.hasNext(); ) { SkinEntry entry = keys.next();
/* 153 */       int slotIndex = entry.slotIndex;
/* 154 */       Slot slot = (Slot)skeleton.slots.get(slotIndex);
/* 155 */       if (slot.attachment == entry.attachment) {
/* 156 */         Attachment attachment = getAttachment(slotIndex, entry.name);
/* 157 */         if (attachment != null) slot.setAttachment(attachment); 
/*     */       }  }
/*     */   
/*     */   }
/*     */   
/*     */   public static class SkinEntry
/*     */   {
/*     */     int slotIndex;
/*     */     String name;
/*     */     Attachment attachment;
/*     */     private int hashCode;
/*     */     
/*     */     SkinEntry() {
/* 170 */       set(0, "");
/*     */     }
/*     */     
/*     */     SkinEntry(int slotIndex, String name, Attachment attachment) {
/* 174 */       set(slotIndex, name);
/* 175 */       this.attachment = attachment;
/*     */     }
/*     */     
/*     */     void set(int slotIndex, String name) {
/* 179 */       if (name == null) throw new IllegalArgumentException("name cannot be null."); 
/* 180 */       this.slotIndex = slotIndex;
/* 181 */       this.name = name;
/* 182 */       this.hashCode = name.hashCode() + slotIndex * 37;
/*     */     }
/*     */     
/*     */     public int getSlotIndex() {
/* 186 */       return this.slotIndex;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getName() {
/* 191 */       return this.name;
/*     */     }
/*     */     
/*     */     public Attachment getAttachment() {
/* 195 */       return this.attachment;
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 199 */       return this.hashCode;
/*     */     }
/*     */     
/*     */     public boolean equals(Object object) {
/* 203 */       if (object == null) return false; 
/* 204 */       SkinEntry other = (SkinEntry)object;
/* 205 */       if (this.slotIndex != other.slotIndex) return false; 
/* 206 */       if (!this.name.equals(other.name)) return false; 
/* 207 */       return true;
/*     */     }
/*     */     
/*     */     public String toString() {
/* 211 */       return this.slotIndex + ":" + this.name;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\Skin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */