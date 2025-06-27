package com.example.websitebantuonggolumiwood.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderNoteDTO {
    private Integer orderId;
    private String orderNote;
}
