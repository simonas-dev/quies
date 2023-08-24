package dev.simonas.quies

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.simonas.quies.data.DataSource
import dev.simonas.quies.data.EncryptedDataSource
import dev.simonas.quies.data.GameSetRepository
import dev.simonas.quies.data.QuestionRepository
import dev.simonas.quies.data.SourceGameSetRepository
import dev.simonas.quies.data.SourceQuestionsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DataModule {

    @Singleton
    @Provides
    fun dataSource(
        @ApplicationContext context: Context,
    ): DataSource =
        EncryptedDataSource(
            context = context,
        )

    @Singleton
    @Provides
    fun questionRepository(
        source: DataSource,
    ): QuestionRepository =
        SourceQuestionsRepository(
            dataSource = source,
        )

    @Singleton
    @Provides
    fun gameSetRepository(
        source: DataSource,
    ): GameSetRepository =
        SourceGameSetRepository(
            dataSource = source,
        )
}
