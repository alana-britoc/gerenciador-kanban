const NOMES_PRIORIDADE = { BAIXA: 'Baixa', MEDIA: 'Média', ALTA: 'Alta' }

function formatarData(iso) {
  const [ano, mes, dia] = iso.split('-')
  return `${dia}/${mes}/${ano}`
}

function hojeIso() {
  return new Date().toLocaleDateString('sv-SE')
}

export default function ExtrasCartao({ extras, coluna }) {
  if (!extras) return null
  const { prazo, prioridade, etiquetas = [] } = extras
  if (!prazo && !prioridade && etiquetas.length === 0) return null

  const atrasado = prazo && coluna !== 'FEITO' && prazo < hojeIso()

  return (
    <div className="extras">
      {prioridade && (
        <span className={`extra prioridade prioridade-${prioridade.toLowerCase()}`}>
          {NOMES_PRIORIDADE[prioridade]}
        </span>
      )}
      {prazo && (
        <span className={`extra prazo${atrasado ? ' atrasado' : ''}`} title={atrasado ? 'Prazo vencido' : 'Prazo'}>
          até {formatarData(prazo)}
        </span>
      )}
      {etiquetas.map((etiqueta) => (
        <span key={etiqueta} className="extra etiqueta">{etiqueta}</span>
      ))}
    </div>
  )
}
