package com.mosquizto.api.repository;

import com.mosquizto.api.model.CollectionItem;
import org.hibernate.query.criteria.JpaCollectionJoin;
import org.springframework.stereotype.Repository;

@Repository
interface CollectionItemRepository extends JpaCollectionJoin<CollectionItem,Integer> {
}
