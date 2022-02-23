package jpql;

import javax.persistence.*;
import java.util.List;

public class JpqlMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            /**
             *  1. 기본 문법과 쿼리 API
             *

            TypedQuery<Member> query1 = em.createQuery("select m from Member m", Member.class);
            TypedQuery<String> query2 = em.createQuery("select m.userName, m.age from Member m where m.id = 10", String.class);
            Query query3 = em.createQuery("select m.userName, m.age from Member m");

            Member query4 = em.createQuery("select m from Member m where m.userName = :username", Member.class)
                .setParameter("username", "mem")
                    .getSingleResult();

            List<Member> resultList = query1.getResultList();
            String singleResult = query2.getSingleResult();

            for (Member member1 : resultList) {
                System.out.println("member1.toString() = " + member1.toString());
            }

            System.out.println("singleResult = " + singleResult);
            */

            /**
             *  2. 프로젝션
             */
//            Member member = new Member();
//            member.setUserName("member1");
//            em.persist(member);
//
//            em.flush();
//            em.clear();

            /** 위에서 영속성을 삭제한후 아래를 실행하면? 영속성이 새로 생길까? */

            //List<Member> resultMember = em.createQuery("select m from Member m", Member.class).getResultList();

            //Member findMember = resultMember.get(0);
            //findMember.setAge(20);

            //List<Team> result2 = em.createQuery("select m.team from Member m", Team.class).getResultList();
            //-> 위 예제는 아래처럼 사용하는게 좋다
            //List<Team> result2convert = em.createQuery("select t from Member m join Team t", Team.class).getResultList();

            //임베디드 프로젝션
            //em.createQuery("select o.address from Order o", Address.class).getResultList();

            /** 여러 값 조회 **/
            /**
            List<Object[]> resultList = em.createQuery("select m.userName, m.age from Member m").getResultList();

            // Object[]
            Object[] result = resultList.get(0);
            System.out.println("userName  = " + result[0] );
            System.out.println("age  = " + result[1] );

            // new명령어로 조회
            List<MemberDTO> resultNew = em.createQuery("select new jpql.MemberDTO(m.userName, m.age) from Member m", MemberDTO.class).getResultList();

            MemberDTO memberDTO = resultNew.get(0);
            System.out.println("memberDTO.getUserName() = " + memberDTO.getUserName());
            System.out.println("memberDTO.getAge() = " + memberDTO.getAge());

            */

            /**
             * 페이징 API
             */
            /*
            for (int i = 0; i < 100; i++) {
                Member member = new Member();
                member.setUserName("member"+i);
                member.setAge(i);
                em.persist(member);
            }

            em.flush();
            em.clear();

            String jpql = "select m from Member m order by m.userName desc";
            List<Member> resultList = em.createQuery(jpql, Member.class)
                    .setFirstResult(0)
                    .setMaxResults(10)
                    .getResultList();
            System.out.println("resultList.size() = " + resultList.size());
            for (Member member1 : resultList) {
                System.out.println("member1 = " + member1);
            }
            */

            /** 조인 **/
            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUserName("관리자e");
            member.setAge(10);
            member.setType(MemberType.ADMIN);

            member.setTeam(team);

            em.persist(member);

            em.flush();
            em.clear();

            //내부 join
            String query = "select m from Member m inner join m.team t";
            //String query = "select m from Member m inner join m.team t where t.name = :teamName"; teamName값을 세팅할때 사용
            List<Member> joinResult = em.createQuery(query, Member.class)
                    .getResultList();

            /**
             * Hibernate:
             *     /* select
             *         m
             *     from
             *         Member m
             *     inner join
             *         m.team t /
             *         select
             *              member0_.id as id1_0_,
             *              member0_.age as age2_0_,
             *              member0_.TEAM_ID as team_id4_0_,
             *              member0_.userName as username3_0_
             *           from
             *              Member member0_
             *           inner join
             *              Team team1_
             *           on member0_.TEAM_ID = team1_.id
             * Hibernate:
             *select
                    * team0_.id as id1_3_0_,
             *team0_.name as name2_3_0_
                    * from
                    * Team team0_
                    * where
                    * team0_.id =?
             */
            // 왜 동작시키지 않은 Team select쿼리가 나갔을까? -> 양방향 연결 시 1대다 에서 fetch를 LAZY상태로 만들어줘야한다.

            /*
            //외부(left, outer) join
            String query2 = "select m from Member m left join m.team t";
            List<Member> joinResult2 = em.createQuery(query2, Member.class)
                    .getResultList();

            //세타(cross) join
            String query3 = "select m from Member m, Team t where m.userName = t.name";
            List<Member> joinResult3 = em.createQuery(query3, Member.class)
                    .getResultList();

            //조인 대상 필터링
            String query4 = "select m from Member m left join m.team t on t.name = 'teamA'";
            List<Member> joinResult4 = em.createQuery(query4, Member.class)
                    .getResultList();

            //연관관계 없는 엔티티 외부 조인
            String query5 = "select m from Member m left join Team t on m.userName = t.name";
            List<Member> joinResult5 = em.createQuery(query5, Member.class)
                    .getResultList();

            System.out.println("joinResult5 = " + joinResult5.size());
            */
            //test
            String query6 = "select m.userName, 'HELLO', true FROM Member m " +
                    "where m.type = jpql.MemberType.ADMIN";
            List<Object[]> testResult = em.createQuery(query6).getResultList();



            for (Object[] o : testResult) {
                System.out.println("o[0] = " + o[0]);
                System.out.println("o[0] = " + o[1]);
                System.out.println("o[0] = " + o[2]);
            }
            /**
             Hibernate:
             /* select
             m.userName,
             'HELLO',
             true
             FROM
             Member m
             where
             m.type = jpql.MemberType.ADMIN / select
            member0_.userName as col_0_0_,
                    'HELLO' as col_1_0_,
            1 as col_2_0_
            from
            Member member0_
            where
            member0_.type=0 */

            //member0_.type=0 으로 나올떈 Enummerrated 옵션을 String으로 주어야한다.

            String str = "123456789";

            System.out.println("split = " + java.util.Arrays.toString(str.split("(?<=\\G....)")));

            //CASE
            String query7 = "select " +
                    "case when m.age <= 10 then '학생요금' " +
                    "     when m.age >= 60 then '경로요금' " +
                    "else '일반요금' " +
                    "end " +
                    "from Member m";

            List<String> result7 = em.createQuery(query7, String.class).getResultList();

            for (String s : result7) {
                System.out.println("s = " + s);
            }

            //coalesce , nullif
            String query8 = "select coalesce(m.userName, '이름 없는 회원') from Member m";
            List<String> result8 = em.createQuery(query8, String.class).getResultList();

            for (String s : result8) {
                System.out.println("s = " + s);
            }

            String query9 = "select nullif(m.userName, '관리자') from Member m";
            List<String> result9 = em.createQuery(query9, String.class).getResultList();

            for (String s : result9) {
                System.out.println("s = " + s);
            }

            Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");
            em.persist(teamB);

            Member memberA = new Member();
            memberA.setUserName("회원1");
            memberA.setAge(0);
            memberA.setTeam(teamA);
            em.persist(memberA);

            Member memberB = new Member();
            memberB.setUserName("회원2");
            memberB.setAge(0);
            memberB.setTeam(teamA);
            em.persist(memberB);

            Member memberC = new Member();
            memberC.setUserName("회원3");
            memberC.setAge(0);
            memberC.setTeam(teamB);
            em.persist(memberC);

            em.flush();
            em.clear();

            String query10 = "select m from Member m";
            String query11 = "select m from Member m join fetch m.team";
            String query12 = "select t from Team t join fetch t.memberList";
            String query13 = "select distinct t from Team t join fetch t.memberList m";
            String query14 = "select t from Team t";
            //List<Member> resultQ10 = em.createQuery(query10, Member.class).getResultList();
            //List<Member> resultQ11 = em.createQuery(query11, Member.class).getResultList();
            //List<Team> resultQ12 = em.createQuery(query12, Team.class).getResultList();
            //List<Team> resultQ13 = em.createQuery(query13, Team.class).getResultList();
            //List<Team> resultQ14 = em.createQuery(query14, Team.class).setFirstResult(0).setMaxResults(2).getResultList();

            //named Query
            //List<Member> namedQuery = em.createNamedQuery("Member.findByUsername", Member.class).setParameter("userName", "회원1").getResultList();
            //System.out.println("namedQuery = " + namedQuery);

            //for(Member member1 : query)
            //for (Team team1 : resultQ14) {
                //System.out.println("member = " + memberR.getUserName() + ", " + memberR.getTeam().getName());
                //System.out.println("team1 = " + team1.getName() + " | members= " + team1.getMemberList().size());
                //for (Member memberIn : team1.getMemberList()) {
                //    System.out.println(" >>> member = " + memberIn);
                //}
            //}

            /** 1. query 10, 11
             * 위 소스 대로라면 쿼리가 Member조회 1번 , 팀A조회 1번 팀B조회 1번 총 3번 날아간다.
             * 만약.. 회원이 100 명이라면? N + 1
             * fetch join 을 사용하면 쿼리가 1번의 쿼리로 끝낸다.

             * 2. query 12
             * team1 = teamA | members= 1
             *  >>> member = Member{id=2, userName='관리자e', age=10}
             * team1 = 팀A | members= 2
             *  >>> member = Member{id=5, userName='회원1', age=0}
             *  >>> member = Member{id=6, userName='회원2', age=0}
             * team1 = 팀A | members= 2
             *  >>> member = Member{id=5, userName='회원1', age=0}
             *  >>> member = Member{id=6, userName='회원2', age=0}
             * team1 = 팀B | members= 1
             *  >>> member = Member{id=7, userName='회원3', age=0}
             *
             *  같은 팀A의 멤버 수 2명인데이터가 2명이라 2번 중복 출력되는데, 해당사항은 DISTINCT로 중복 제거 가능
             *
             *  3. query 13
             *  team1 = teamA | members= 1
             *  >>> member = Member{id=2, userName='관리자e', age=10}
             * team1 = 팀A | members= 2
             *  >>> member = Member{id=5, userName='회원1', age=0}
             *  >>> member = Member{id=6, userName='회원2', age=0}
             * team1 = 팀B | members= 1
             *  >>> member = Member{id=7, userName='회원3', age=0}
             *
             *  query 12번에서 발생한 중복 제거 쿼리로보면 중복이 전부 제거되지 않지만, JPQL이 엔티티에 모든값이 동일한 값을 제거 해준다.
             *
             */

            /**
             * 벌크 연산
             */
            String qlString = "update Member m set m.age = 20";
            int resultCount = em.createQuery(qlString).executeUpdate();

            //update수행 건수
            System.out.println("resultCount = " + resultCount);

            //em.clear();

            Member member1 = em.find(Member.class, memberA.getId());
            System.out.println("member1.getAge = " + member1.getAge());

            /**
                실행하면 이미 위에 회원1,2,3의 정보를 가지고 있는 나이가 업데이트 된다. ( DB에만 )
                하지만 memberA,B,C에 나이를 지정하지 않았는데 업데이트가 되는 것을 확인할 수 있다.
                영속성컨텍스트에 있는 memberA의 나이를 조회하면 0으로 조회된다.

                 * .executeUpdate()선언되어있다면 그 위에서 자동으로 flash가 수행된다.

                이 사항을 막기 위해선 영속성컨텍스트를 clear해줘야한다.

             */



            tx.commit();
        } catch (Exception e){
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        emf.close();
    }
}
