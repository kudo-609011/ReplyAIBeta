package com.example.data.model

object ReplyArchetypeList {
  val all = listOf(
    ReplyArchetype(
      id = "1-line",
      name = "1-Line",
      description = "Ultra-compact single sentence reply with maximum punch.",
      example = "\"Gandhi led India to freedom through nonviolent civil resistance like the Salt March.\""
    ),
    ReplyArchetype(
      id = "2-line",
      name = "2-Line",
      description = "Exactly two balanced sentences delivering clear context and point.",
      example = "\"Gandhi united millions across India through the philosophy of Satyagraha. His nonviolent campaigns dismantled British colonial authority.\""
    ),
    ReplyArchetype(
      id = "single-word",
      name = "Single-Word",
      description = "Strictly one powerful, unambiguous word.",
      example = "\"Satyagraha\""
    ),
    ReplyArchetype(
      id = "debate",
      name = "Debate",
      description = "Persuasive, sharp counterpoints highlighting inconsistencies in premises.",
      example = "\"Legal liberation removes state-sponsored barriers; it does not automatically erase ingrained social biases without active reform.\""
    ),
    ReplyArchetype(
      id = "funny",
      name = "Funny",
      description = "Witty and humorous reply without offensive language.",
      example = "\"Freedom came with a full user manual, but some people apparently skipped the chapter on equality.\""
    ),
    ReplyArchetype(
      id = "arrogant",
      name = "Arrogant",
      description = "Smug, superior tone that playfully belittles opposing logic.",
      example = "\"If you honestly have to ask what Gandhi did, you might want to open a history book before debating.\""
    ),
    ReplyArchetype(
      id = "lord",
      name = "Lord",
      description = "Aristocratic, regal demeanor speaking from majestic superiority.",
      example = "\"It is self-evident to any sovereign mind that his grace mobilized a continent with sheer spiritual fortitude.\""
    ),
    ReplyArchetype(
      id = "passive",
      name = "Passive",
      description = "Non-confrontational, accommodating, and mild-mannered.",
      example = "\"Well, if you look at it, his peaceful protests seemed to help bring people together for independence.\""
    ),
    ReplyArchetype(
      id = "logical",
      name = "Logical",
      description = "Structured, rational, and evidence-oriented reasoning.",
      example = "\"Freedom is a constitutional guarantee of civil equality, whereas prejudice is a behavioral issue that requires cultural evolution.\""
    ),
    ReplyArchetype(
      id = "respectful",
      name = "Respectful",
      description = "Polite, calm, empathetic, and constructive tone.",
      example = "\"While legal systems grant fundamental rights to all citizens, overcoming historical social attitudes remains an ongoing societal journey.\""
    ),
    ReplyArchetype(
      id = "counterargument",
      name = "Counterargument",
      description = "Directly addresses counter-claims with rigorous analytical critique.",
      example = "\"The existence of discrimination does not negate constitutional freedom; rather, it highlights the gap between legal theory and social reality.\""
    ),
    ReplyArchetype(
      id = "short",
      name = "Short",
      description = "Direct, high-impact punchy responses.",
      example = "\"Laws mandate civil freedom; culture decides how fairly people treat each other.\""
    ),
    ReplyArchetype(
      id = "casual",
      name = "Casual",
      description = "Natural, conversational everyday style.",
      example = "\"Having legal freedom is one thing, but changing age-old mindsets takes time and honest conversations.\""
    ),
    ReplyArchetype(
      id = "formal",
      name = "Formal",
      description = "Professional, academic, and articulate phrasing.",
      example = "\"De jure equality guaranteed by constitutional frameworks often diverges from de facto social integration.\""
    ),
    ReplyArchetype(
      id = "detailed",
      name = "Detailed",
      description = "Comprehensive, multi-point explanation exploring nuances.",
      example = "\"1. Constitutional freedom establishes legal equality before the law. 2. Social hierarchies persist due to generational conditioning. 3. Systemic reform requires both policy enforcement and educational initiatives.\""
    )
  )
}
