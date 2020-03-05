package pt.up.hs.project.service.importer.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Example;
import pt.up.hs.project.domain.Label;
import pt.up.hs.project.domain.Participant;
import pt.up.hs.project.repository.LabelRepository;
import pt.up.hs.project.repository.ParticipantRepository;
import pt.up.hs.project.service.importer.batch.processor.ImportLoggingProcessor;
import pt.up.hs.project.utils.Dates;
import pt.up.hs.project.utils.Genders;
import pt.up.hs.project.utils.HandwritingMeans;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class ParticipantCsvImportConfig {
    /*private static final String DEFAULT_DELIMITER = ",";
    private static final String DEFAULT_LABEL_DELIMITER = ";";

    private static final String HEADER_NAME = "name";
    private static final String HEADER_GENDER = "gender";
    private static final String HEADER_HANDEDNESS = "handedness";
    private static final String HEADER_BIRTHDATE = "birthdate";
    private static final String HEADER_ADDITIONAL_INFO = "additional_info";
    private static final String HEADER_LABELS = "labels";

    private static final String[] DEFAULT_HEADERS = new String[] {
        HEADER_NAME, HEADER_GENDER, HEADER_HANDEDNESS, HEADER_BIRTHDATE, HEADER_LABELS, HEADER_ADDITIONAL_INFO
    };

    private ParticipantRepository participantRepository;
    private LabelRepository labelRepository;

    @Autowired
    public ParticipantCsvImportConfig(
        ParticipantRepository participantRepository, LabelRepository labelRepository
    ) {
        this.participantRepository = participantRepository;
        this.labelRepository = labelRepository;
    }

    @Bean
    @StepScope
    ItemReader<Participant> participantCsvImportItemReader(
        @Value("#{jobParameters['file.path']}") String pathToFile,
        @Value("#{jobParameters['delimiter']}") String delim,
        @Value("#{jobParameters['hasHeader']}") Boolean hasHeader
    ) {
        FlatFileItemReader<Participant> csvFileReader = new FlatFileItemReader<>();
        csvFileReader.setResource(new FileSystemResource(pathToFile));
        csvFileReader.setLinesToSkip(hasHeader ? 1 : 0);

        LineMapper<Participant> participantLineMapper = createParticipantLineMapper(
            delim == null ? DEFAULT_DELIMITER : delim
        );
        csvFileReader.setLineMapper(participantLineMapper);

        return csvFileReader;
    }

    private LineMapper<Participant> createParticipantLineMapper(String delim) {
        DefaultLineMapper<Participant> participantLineMapper = new DefaultLineMapper<>();

        LineTokenizer participantLineTokenizer = createParticipantLineTokenizer(delim);
        participantLineMapper.setLineTokenizer(participantLineTokenizer);

        FieldSetMapper<Participant> participantInformationMapper = createParticipantInformationMapper();
        participantLineMapper.setFieldSetMapper(participantInformationMapper);

        return participantLineMapper;
    }

    private LineTokenizer createParticipantLineTokenizer(String delim) {
        DelimitedLineTokenizer participantLineTokenizer = new DelimitedLineTokenizer();
        participantLineTokenizer.setDelimiter(delim);
        participantLineTokenizer.setNames(DEFAULT_HEADERS);
        participantLineTokenizer.setStrict(false);
        return participantLineTokenizer;
    }

    private FieldSetMapper<Participant> createParticipantInformationMapper() {
        return fs -> {
            Participant participant = new Participant();
            participant.setName(fs.readString(HEADER_NAME));
            participant.setGender(Genders.fromString(fs.readString(HEADER_GENDER)));
            participant.setHandedness(
                HandwritingMeans.fromString(fs.readString(HEADER_HANDEDNESS))
            );
            participant.setBirthdate(
                Dates.convertToLocalDate(fs.readDate(HEADER_BIRTHDATE))
            );
            participant.setAdditionalInfo(fs.readString(HEADER_ADDITIONAL_INFO));

            String labelNames = fs.readString(HEADER_LABELS);
            if (!labelNames.trim().isEmpty()) {
                participant.setLabels(getLabelsByNames(labelNames.split(DEFAULT_LABEL_DELIMITER)));
            }
            return participant;
        };
    }

    @Bean
    ItemProcessor<Participant, Participant> participantCsvImportLoggingProcessor() {
        return new ImportLoggingProcessor<>();
    }

    @Bean
    ItemWriter<Participant> participantCsvImportItemWriter() {

        return participants -> {
            for (Participant participant: participants) {
                participantRepository.save(participant);
            }
        };
    }

    @Bean
    Step participantCsvImportStep(
        ItemReader<Participant> participantCsvImportItemReader,
        ItemProcessor<Participant, Participant> participantCsvImportLoggingProcessor,
        ItemWriter<Participant> participantCsvImportItemWriter,
        StepBuilderFactory stepBuilderFactory
    ) {
        return stepBuilderFactory.get("participantCsvImportStep")
            .<Participant, Participant>chunk(1)
            .reader(participantCsvImportItemReader)
            .processor(participantCsvImportLoggingProcessor)
            .writer(participantCsvImportItemWriter)
            .build();
    }

    @Bean
    Job participantCsvImportJob(
        JobBuilderFactory jobBuilderFactory,
        @Qualifier("participantCsvImportStep") Step participantCsvImportStep
    ) {
        return jobBuilderFactory.get("participantCsvImportJob")
            .incrementer(new RunIdIncrementer())
            .flow(participantCsvImportStep)
            .end()
            .build();
    }

    *//* Helper Functions *//*

    private Set<Label> getLabelsByNames(String[] names) {

        return Arrays.stream(names)
            .filter(name -> !name.trim().isEmpty())
            .map(name -> {
                Label label = new Label();
                label.setName(name);
                return labelRepository.findOne(Example.of(label)).orElse(label);
            })
            .collect(Collectors.toSet());
    }*/
}
