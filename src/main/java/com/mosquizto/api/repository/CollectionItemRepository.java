package com.mosquizto.api.repository;

import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.CollectionItem;
import org.hibernate.query.criteria.JpaCollectionJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;

@Repository
public interface CollectionItemRepository extends JpaRepository<CollectionItem,Integer> {
    List<CollectionItem> findByCollectionId(Integer collectionId);
}
