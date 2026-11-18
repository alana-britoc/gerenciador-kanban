import { CalendarDays, Tag } from 'lucide-react'

function formatarData(iso) {
  const [ano, mes, dia] = iso.split('-')
  return `${dia}/${mes}/${ano}`
}

function hojeIso() {
  return new Date().toLocaleDateString('sv-SE')
}

export default function ExtrasCartao({ id, extras, coluna }) {
  const { prazo, etiquetas = [] } = extras ?? {}
  const vencido = prazo && coluna !== 'FEITO' && prazo < hojeIso()

  return (
    <footer className="cartao-rodape">
      {prazo && (
        <span className={`rodape-item${vencido ? ' prazo-vencido' : ''}`} title={vencido ? 'Prazo vencido' : 'Prazo'}>
          <CalendarDays size={13} /> {formatarData(prazo)}
        </span>
      )}
      {etiquetas.map((etiqueta) => (
        <span key={etiqueta} className="etiqueta">
          <Tag size={10} /> {etiqueta}
        </span>
      ))}
      <span className="cartao-id">#{id}</span>
    </footer>
  )
}
