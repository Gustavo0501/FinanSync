package com.gustavo.finansync.service;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class GmailService {

    public GmailService() {}

    /**
     * Busca anexos CSV em e-mails do Gmail usando critérios de remetente e assunto.
     * @param gmail Objeto Gmail autenticado para o usuário
     * @param userId ID do usuário ("me" para o próprio usuário autenticado)
     * @param remetente E-mail do remetente (ex: banco)
     * @param assunto Assunto do e-mail (ex: "Extrato Mensal")
     * @return Lista de InputStream dos arquivos CSV encontrados
     */
    public List<InputStream> buscarAnexosCsv(Gmail gmail, String userId, String remetente, String assunto) throws Exception {
        List<InputStream> anexos = new ArrayList<>();
        String query = "from:" + remetente + " subject:\"" + assunto + "\" has:attachment";
        ListMessagesResponse response = gmail.users().messages().list(userId).setQ(query).execute();

        if (response.getMessages() == null) return anexos;

        for (Message msg : response.getMessages()) {
            Message message = gmail.users().messages().get(userId, msg.getId()).setFormat("full").execute();
            List<MessagePart> parts = message.getPayload().getParts();
            if (parts == null) continue;
            for (MessagePart part : parts) {
                if (part.getFilename() != null && part.getFilename().endsWith(".csv")) {
                    String attId = part.getBody().getAttachmentId();
                    MessagePartBody attachPart = gmail.users().messages().attachments()
                            .get(userId, msg.getId(), attId).execute();
                    byte[] fileByteArray = Base64.getDecoder().decode(attachPart.getData());
                    anexos.add(new ByteArrayInputStream(fileByteArray));
                }
            }
        }
        return anexos;
    }
}
