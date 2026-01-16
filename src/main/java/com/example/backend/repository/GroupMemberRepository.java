package com.example.backend.repository;

import com.example.backend.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
        @Query("""
        select gm.group.id
        from GroupMember gm
        where gm.intern.id = :internId and gm.leftAt is null
    """)
    List<Long> findActiveGroupIdsByIntern(Long internId);

    @Query("""
        select (count(gm) > 0)
        from GroupMember gm
        where gm.intern.id = :internId and gm.group.id = :groupId and gm.leftAt is null
    """)
    boolean isInternActiveInGroup(Long internId, Long groupId);
}
