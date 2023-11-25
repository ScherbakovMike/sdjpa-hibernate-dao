package guru.springframework.jdbc.dao

import guru.springframework.jdbc.domain.Author
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.TypedQuery
import org.springframework.stereotype.Component

/**
 * Created by jt on 8/28/21.
 */
@Component
class AuthorDaoImpl(private val entityManagerFactory: EntityManagerFactory) : AuthorDao {
    override fun getById(id: Long): Author? = getEntityManager().find(Author::class.java, id)


    override fun findAuthorByName(firstName: String, lastName: String): Author? {
        val query: TypedQuery<Author> = getEntityManager().createQuery(
            "SELECT a from Author a WHERE a.firstName = :first_name and a.lastName = :last_name",
            Author::class.java
        )
        query.setParameter("first_name", firstName)
        query.setParameter("last_name", lastName)
        return query.singleResult
    }

    override fun saveNewAuthor(author: Author): Author? {
        val entityManager = getEntityManager()
        entityManager.transaction.begin()
        entityManager.persist(author)
        entityManager.flush()
        entityManager.transaction.commit()
        return author
    }

    override fun updateAuthor(author: Author): Author? {
        val entityManager = getEntityManager()
        entityManager.joinTransaction()
        entityManager.merge(author)
        entityManager.flush()
        entityManager.clear()
        return entityManager.find(Author::class.java, author.id)
    }

    override fun deleteAuthorById(id: Long) {
        val entityManager = getEntityManager()
        entityManager.transaction.begin()
        val author = entityManager.find(Author::class.java, id)
        entityManager.remove(author)
        entityManager.flush()
        entityManager.transaction.commit()
    }

    private fun getEntityManager() = entityManagerFactory.createEntityManager()
}
