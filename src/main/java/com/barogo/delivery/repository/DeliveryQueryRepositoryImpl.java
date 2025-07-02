package com.barogo.delivery.repository;

import com.barogo.common.exception.InvalidRequestException;
import com.barogo.delivery.entity.Delivery;
import com.barogo.delivery.dto.DeliverySearchRequest;
import com.barogo.delivery.enums.DeliveryStatus;
import com.barogo.user.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import java.util.List;
import static com.barogo.delivery.entity.QDelivery.delivery;

@RequiredArgsConstructor
public class DeliveryQueryRepositoryImpl implements DeliveryQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Delivery> searchDeliveries(User user, DeliverySearchRequest request, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder()
                .and(delivery.user.eq(user))
                .and(delivery.isDeleted.isFalse())
                .and(delivery.createdAt.between(
                        request.getStart().atStartOfDay(),
                        request.getEnd().plusDays(1).atStartOfDay()
                ));

        if (StringUtils.hasText(request.getStatus())) {
            try {
                DeliveryStatus statusEnum = DeliveryStatus.valueOf(request.getStatus().toUpperCase());
                builder.and(delivery.status.eq(statusEnum));
            } catch (IllegalArgumentException e) {
                throw new InvalidRequestException("잘못된 배송 상태 값입니다. " + request.getStatus());
            }
        }

        if (StringUtils.hasText(request.getAddress())) {
            builder.and(delivery.address.contains(request.getAddress()));
        }

        List<Delivery> content = queryFactory.selectFrom(delivery)
                .where(builder)
                .orderBy(getSortOrder(request.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory.select(delivery.count())
                .from(delivery)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private OrderSpecifier<?> getSortOrder(String sort) {
        if ("asc".equalsIgnoreCase(sort)) {
            return delivery.createdAt.asc();
        } else {
            return delivery.createdAt.desc();
        }
    }
}