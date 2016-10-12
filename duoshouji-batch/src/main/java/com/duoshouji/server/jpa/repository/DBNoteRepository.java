package com.duoshouji.server.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duoshouji.server.jpa.entity.DBNote;

@Repository
public interface DBNoteRepository extends JpaRepository<DBNote, Long>{
	public List<DBNote> findByUserId(Long userId);
}
