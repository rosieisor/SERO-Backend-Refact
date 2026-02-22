package com.werp.sero.order.query.controller;

import com.werp.sero.order.query.dto.*;
import com.werp.sero.security.annotation.CurrentUser;
import com.werp.sero.security.principal.CustomUserDetails;
import com.werp.sero.order.query.service.SOClientQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "주문(고객사) - Query", description = "고객 주문 관련 API")
@RequestMapping("/clients/orders")
@RequiredArgsConstructor
@RestController
public class SOClientQueryController {

    private final SOClientQueryService soClientService;

    @Operation(summary = "고객사 주문 이력 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "고객사 주문 이력 조회", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = SOResponseDTO.class)
                    )
            )),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "CLIENT_CANNOT_FOUND", value = """
                            {
                                "code": "CLIENT001",
                                "message": "고객사 정보를 찾을 수 없습니다."
                            }
                            """)
            }))
    })
    @GetMapping("/history")
    public ResponseEntity<List<SOClientResponseDTO>> findOrderHistory(
            @CurrentUser final CustomUserDetails user) {

        final List<SOClientResponseDTO> response = soClientService.findOrderHistory(user.getClientId());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "고객사 기존 주문 불러오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "고객사 기존 주문 불러오기 성공", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = SOResponseDTO.class)
                    )
            )),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "SALES_ORDER_CANNOT_FOUND", value = """
                            {
                                "code": "ORDER002",
                                "message": "주문 정보를 찾을 수 없습니다."
                            }
                            """)
            }))
    })
    @GetMapping("/{orderId}/copy-info")
    public ResponseEntity<SOClientResponseDTO> getOrderForReorder(
            @PathVariable final int orderId,
            @CurrentUser final CustomUserDetails user) {

        final SOClientResponseDTO response = soClientService.getOrderForReorder(orderId, user.getClientId());

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "고객사 주문 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "고객사 주문 목록 조회", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = SOClientListResponseDTO.class)
                    )
            )),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "SALES_ORDER_LIST_NOT_FOUND", value = """
                            {
                                "code": "ORDER001",
                                "message": "주문 목록을 찾을 수 없습니다."
                            }
                            """)
            }))
    })
    @GetMapping
    public ResponseEntity<List<SOClientListResponseDTO>> getOrderList(
            @CurrentUser final CustomUserDetails user,
            @ModelAttribute SOClientFilterDTO filter,
            @RequestParam(defaultValue = "1") Integer page) {

        List<SOClientListResponseDTO> response = soClientService.findClientOrderList(user.getClientId(), filter, page);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "고객사 주문 상세 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "고객사 주문 상세 조회", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = SOClientDetailResponseDTO.class)
                    )
            )),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "SALES_ORDER_NOT_FOUND", value = """
                            {
                                "code": "ORDER002",
                                "message": "주문 목록을 찾을 수 없습니다."
                            }
                            """)
            }))
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<SOClientDetailResponseDTO> getOrderDetail(
            @PathVariable int orderId,
            @CurrentUser final CustomUserDetails user) {

        SOClientDetailResponseDTO response = soClientService.findClientOrderDetail(orderId, user.getClientId());

        return ResponseEntity.ok(response);
    }


}
