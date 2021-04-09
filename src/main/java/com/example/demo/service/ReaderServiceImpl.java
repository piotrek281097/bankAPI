package com.example.demo.service;

import com.example.demo.domain.Reader;
import com.example.demo.exceptions.ReaderWithThisPeselAlreadyExistsException;
import com.example.demo.exceptions.WrongDataException;
import com.example.demo.repository.ReaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.util.List;

@Service
public class ReaderServiceImpl implements ReaderService {

    private ReaderRepository readerRepository;

    @Autowired
    public ReaderServiceImpl(ReaderRepository readerRepository) {
        this.readerRepository = readerRepository;
    }

    @Override
    public void addReader(Reader reader) {
        try {
            Reader readerFoundByPesel = readerRepository.findByPesel(reader.getPesel());

            if (readerFoundByPesel == null) {
                readerRepository.save(reader)
                ;
            } else {
                throw new ReaderWithThisPeselAlreadyExistsException("Reader with this pesel already exists");
            }
        } catch (ConstraintViolationException | IllegalArgumentException ex) {
            throw new WrongDataException("Wrong data");
        }
    }

    @Override
    public void updateReader(long readerId, Reader reader) {
        readerRepository.save(reader);
    }

    @Override
    public List<Reader> getAllReaders() {
        return null;
    }

    @Override
    public Reader findReaderByPesel(String pesel) {
        return readerRepository.findByPesel(pesel);
    }

    @Override
    public Reader findReaderById(long id) {
        return readerRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteReaderById(long id) {
        readerRepository.deleteById(id);
    }

    @Override
    public Page<Reader> getPageableReaders(int page, int size) {
        return readerRepository.findAll(PageRequest.of(page, size));
    }
}
