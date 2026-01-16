package com.example.backend.repository;

import com.example.backend.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    @Query("""
        select gm from GroupMember gm
        where gm.intern.id = :internId
          and gm.leftAt is null
        order by gm.joinedAt desc
    """)
    Optional<GroupMember> findActiveMembership(@Param("internId") Long internId);

    @Query("""
        select gm.group.id
        from GroupMember gm
        where gm.intern.id = :internId
          and gm.leftAt is null
        order by gm.joinedAt desc
    """)
    Optional<Long> findActiveGroupIdOfIntern(Long internId);

    @Query("""
        select g.id
        from ProgramGroup g
        where g.mentor = :mentorId
    """)
    List<Long> findGroupIdsByMentorId(Long mentorId);
}
