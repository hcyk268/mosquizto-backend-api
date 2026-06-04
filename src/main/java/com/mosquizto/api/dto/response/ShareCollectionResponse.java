package com.mosquizto.api.dto.response;


import com.mosquizto.api.util.AccessStatus;
import com.mosquizto.api.util.CollectionRole;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShareCollectionResponse {
    Long inviterId ;
    String inviterUsername ;
    Integer collectionId ;
    String title ;
    String description ;
    Date inviteAt ; // xem như là lúc mời là lúc tạo luôn
    CollectionRole collectionRole ;
    AccessStatus accessStatus ;
}
