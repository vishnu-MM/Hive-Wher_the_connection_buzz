package com.hive.userservice.DTO;

import com.hive.userservice.Utility.ConnectionStatue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionDTO {
    private Long id;
    private Long senderId;
    private Long recipientId;
    private ConnectionStatue status;
    private Date date;
}
