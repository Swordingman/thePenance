package com.esotericsoftware.spine38.attachments;

import com.esotericsoftware.spine38.Skin;

public interface AttachmentLoader {
  RegionAttachment newRegionAttachment(Skin paramSkin, String paramString1, String paramString2);
  
  MeshAttachment newMeshAttachment(Skin paramSkin, String paramString1, String paramString2);
  
  BoundingBoxAttachment newBoundingBoxAttachment(Skin paramSkin, String paramString);
  
  ClippingAttachment newClippingAttachment(Skin paramSkin, String paramString);
  
  PathAttachment newPathAttachment(Skin paramSkin, String paramString);
  
  PointAttachment newPointAttachment(Skin paramSkin, String paramString);
}


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\attachments\AttachmentLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */