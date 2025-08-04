package com.ali.amara.farm.dto;

import com.ali.amara.core.dto.BaseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FarmEquipmentDTO extends BaseDTO {
    private String name;
    private String model;
    private String manufacturer;
    private Date purchaseDate;
    private String status;
    private String description;
    private String maintenanceSchedule;
    private Double purchasePrice;
    private Long farmId;
}
